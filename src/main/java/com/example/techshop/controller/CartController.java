package com.example.techshop.controller;

import com.example.techshop.domain.User;
import com.example.techshop.service.CartService;
import com.example.techshop.service.ProductService;
import com.example.techshop.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Возвращает текущего пользователя или null, если не аутентифицирован.
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        String username = auth.getName();
        if (username == null || "anonymousUser".equals(username)) {
            return null;
        }
        return userService.findByUsername(username).orElse(null);
    }

    @GetMapping
    public String viewCart(Model model) {

        User user = getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        var items = cartService.getItems(user);

        if (items.isEmpty()) {
            return "cart/empty";
        }

        model.addAttribute("items", items);
        model.addAttribute("total", cartService.getTotal(user));

        return "cart/view";
    }

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id) {
        User user = getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }
        cartService.addToCart(user, id);
        return "redirect:/cart";
    }

    @PostMapping("/removeOne/{id}")
    public String removeOne(@PathVariable Long id) {
        User user = getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }
        cartService.removeOne(user, id);
        return "redirect:/cart";
    }

    @PostMapping("/removeAll/{id}")
    public String removeAll(@PathVariable Long id) {
        User user = getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }
        cartService.removeAll(user, id);
        return "redirect:/cart";
    }
}