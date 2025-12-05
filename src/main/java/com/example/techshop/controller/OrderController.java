package com.example.techshop.controller;

import com.example.techshop.cart.CartItem;
import com.example.techshop.domain.Order;
import com.example.techshop.domain.User;
import com.example.techshop.service.CartService;
import com.example.techshop.service.OrderService;
import com.example.techshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    // checkout form
    @GetMapping("/checkout")
    public String checkoutForm(Model model, HttpSession session, Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }
        List<CartItem> items = cartService.getItems(session).stream().collect(Collectors.toList());
        if (items.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("items", items);
        model.addAttribute("total", cartService.getTotal(session));
        // empty form fields
        model.addAttribute("name", "");
        model.addAttribute("phone", "");
        model.addAttribute("address", "");
        model.addAttribute("comment", "");
        return "order/checkout";
    }

    // submit order
    @PostMapping("/submit")
    public String submitOrder(@RequestParam String name,
                              @RequestParam String phone,
                              @RequestParam String address,
                              @RequestParam(required = false) String comment,
                              HttpSession session,
                              Authentication auth,
                              Model model) {

        if (auth == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartService.getItems(session).stream().collect(Collectors.toList());

        if (cartItems.isEmpty()) {
            model.addAttribute("error", "Корзина пуста");
            return "redirect:/cart";
        }

        Order order = orderService.createOrderFromCart(user, cartItems, name, phone, address, comment);

        // очистить корзину
        session.removeAttribute("CART");

        model.addAttribute("order", order);
        return "order/thanks";
    }

    // list orders
    @GetMapping("/my")
    public String myOrders(Model model, Authentication auth) {
        if (auth == null) return "redirect:/login";

        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("orders", orderService.findOrdersForUser(user));
        return "order/list";
    }

    // order details
    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id, Model model, Authentication auth) {
        if (auth == null) return "redirect:/login";
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.findByIdAndUser(id, user);
        if (order == null) return "redirect:/order/my";

        model.addAttribute("order", order);
        return "order/details";
    }
}