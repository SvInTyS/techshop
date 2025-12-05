package com.example.techshop.controller;

import com.example.techshop.service.OrderService;
import com.example.techshop.service.CartService;
import com.example.techshop.domain.User;
import com.example.techshop.service.UserService;
import com.example.techshop.dto.OrderDTO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(CartService cartService, OrderService orderService, UserService userService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/order/checkout")
    public String checkout(Model model,
                           @AuthenticationPrincipal UserDetails principal) {

        User user = userService.findByUsername(principal.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        model.addAttribute("items", cartService.getItems(user));
        model.addAttribute("total", cartService.getTotal(user));
        model.addAttribute("orderDto", new OrderDTO());

        return "order/checkout";
    }

    @PostMapping("/order/checkout")
    public String placeOrder(@ModelAttribute("orderDto") OrderDTO dto,
                             @AuthenticationPrincipal UserDetails principal) {

        User user = userService.findByUsername(principal.getUsername()).orElse(null);
        if (user == null) return "redirect:/login";

        orderService.createOrder(user, dto);
        cartService.clear(user);

        return "redirect:/order/thanks";
    }

    @GetMapping("/order/thanks")
    public String thanks() {
        return "order/thanks";
    }
}