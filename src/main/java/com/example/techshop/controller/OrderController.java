package com.example.techshop.controller;

import com.example.techshop.dto.OrderDTO;
import com.example.techshop.domain.User;
import com.example.techshop.service.CartService;
import com.example.techshop.service.OrderService;
import com.example.techshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(CartService cartService,
                           OrderService orderService,
                           UserService userService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
    }

    private Optional<User> getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        String username = auth.getName();
        if (username == null || "anonymousUser".equals(username)) return Optional.empty();
        return userService.findByUsername(username);
    }

    @GetMapping("/checkout")
    public String checkout(Model model,
                           Authentication auth,
                           HttpSession session) {

        var userOpt = getCurrentUser(auth);
        if (userOpt.isEmpty()) return "redirect:/login";

        User user = userOpt.get();
        String cartKey = cartService.getCartKey(session);

        // На всякий случай сольём сессионную корзину в user-корзину
        cartService.mergeSessionCartIntoUser(cartKey, user);

        var items = cartService.getUserItems(user);
        if (items.isEmpty()) return "redirect:/cart";

        // Предзаполняем форму из профиля
        OrderDTO orderDto = new OrderDTO();
        String fullName = "";
        if (user.getFirstName() != null) {
            fullName += user.getFirstName();
        }
        if (user.getLastName() != null) {
            if (!fullName.isEmpty()) fullName += " ";
            fullName += user.getLastName();
        }
        if (!fullName.isEmpty()) {
            orderDto.setName(fullName);
        }
        if (user.getPhone() != null) {
            orderDto.setPhone(user.getPhone());
        }

        model.addAttribute("orderDto", orderDto);
        model.addAttribute("items", items);
        model.addAttribute("total", cartService.getUserTotal(user));
        return "order/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@ModelAttribute("orderDto") OrderDTO dto,
                             Authentication auth,
                             HttpSession session,
                             Model model) {

        var userOpt = getCurrentUser(auth);
        if (userOpt.isEmpty()) return "redirect:/login";

        User user = userOpt.get();
        String cartKey = cartService.getCartKey(session);

        // окончательно убедимся, что всё в user-корзине
        cartService.mergeSessionCartIntoUser(cartKey, user);

        var items = cartService.getUserItems(user);
        if (items.isEmpty()) {
            model.addAttribute("error", "Корзина пустая");
            return "order/checkout";
        }

        var order = orderService.createOrderFromCart(
                user,
                items,
                dto.getName(),
                dto.getPhone(),
                dto.getAddress(),
                dto.getComment()
        );

        // очищаем корзины
        cartService.clearUserCart(user);
        cartService.clearSessionCart(cartKey);

        model.addAttribute("orderId", order.getId());
        return "order/success";
    }

    // ЛК: история заказов
    @GetMapping("/history")
    public String orderHistory(Model model, Authentication auth) {
        var userOpt = getCurrentUser(auth);
        if (userOpt.isEmpty()) return "redirect:/login";

        var user = userOpt.get();
        var orders = orderService.findOrdersForUser(user);

        model.addAttribute("orders", orders);
        return "order/history";
    }

    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id,
                               Model model,
                               Authentication auth) {

        var userOpt = getCurrentUser(auth);
        if (userOpt.isEmpty()) return "redirect:/login";

        var user = userOpt.get();
        var order = orderService.findByIdAndUser(id, user);
        if (order == null) return "redirect:/order/history";

        model.addAttribute("order", order);
        return "order/details";
    }
}