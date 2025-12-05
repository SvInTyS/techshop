package com.example.techshop.controller;

import com.example.techshop.dto.OrderDTO;
import com.example.techshop.domain.User;
import com.example.techshop.service.CartService;
import com.example.techshop.service.OrderService;
import com.example.techshop.service.UserService;
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
    public String checkout(Model model, Authentication auth) {
        var userOpt = getCurrentUser(auth);
        if (userOpt.isEmpty()) return "redirect:/login";

        User user = userOpt.get();
        var items = cartService.getItems(user);
        if (items.isEmpty()) return "redirect:/cart";

        model.addAttribute("orderDto", new OrderDTO());
        model.addAttribute("items", items);
        model.addAttribute("total", cartService.getTotal(user));
        return "order/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@ModelAttribute("orderDto") OrderDTO dto,
                             Authentication auth,
                             Model model) {

        var userOpt = getCurrentUser(auth);
        if (userOpt.isEmpty()) return "redirect:/login";

        User user = userOpt.get();
        var items = cartService.getItems(user);
        if (items.isEmpty()) {
            model.addAttribute("error", "Корзина пустая");
            return "order/checkout";
        }

        // Вызываем существующий в твоём сервисе createOrderFromCart
        var order = orderService.createOrderFromCart(
                user,
                items,
                dto.getName(),
                dto.getPhone(),
                dto.getAddress(),
                dto.getComment()
        );

        cartService.clear(user);

        model.addAttribute("orderId", order.getId());
        return "order/success";
    }
    //Методы ЛК пользователей
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