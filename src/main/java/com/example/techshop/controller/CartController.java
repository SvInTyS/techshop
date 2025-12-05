package com.example.techshop.controller;

import com.example.techshop.domain.User;
import com.example.techshop.service.CartService;
import com.example.techshop.service.ProductService;
import com.example.techshop.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;

    public CartController(CartService cartService,
                          ProductService productService,
                          UserService userService) {
        this.cartService = cartService;
        this.productService = productService;
        this.userService = userService;
    }

    private Optional<User> getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        String username = auth.getName();
        if (username == null || "anonymousUser".equals(username)) return Optional.empty();
        return userService.findByUsername(username);
    }

    @GetMapping
    public String viewCart(Model model, Authentication auth) {
        var userOpt = getCurrentUser(auth);
        if (userOpt.isEmpty()) return "redirect:/login";

        User user = userOpt.get();
        var items = cartService.getItems(user);
        if (items.isEmpty()) {
            return "cart/empty";
        }

        model.addAttribute("items", items);
        model.addAttribute("total", cartService.getTotal(user));
        return "cart/view";
    }

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, Authentication auth) {
        var userOpt = getCurrentUser(auth);
        if (userOpt.isEmpty()) return "redirect:/login";

        User user = userOpt.get();
        cartService.addToCart(user, id);
        return "redirect:/cart";
    }

    @PostMapping("/removeOne/{id}")
    public String removeOne(@PathVariable Long id, Authentication auth) {
        var userOpt = getCurrentUser(auth);
        if (userOpt.isEmpty()) return "redirect:/login";

        User user = userOpt.get();
        cartService.removeOne(user, id);
        return "redirect:/cart";
    }

    @PostMapping("/removeAll/{id}")
    public String removeAll(@PathVariable Long id, Authentication auth) {
        var userOpt = getCurrentUser(auth);
        if (userOpt.isEmpty()) return "redirect:/login";

        User user = userOpt.get();
        cartService.removeAll(user, id);
        return "redirect:/cart";
    }
}