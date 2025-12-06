package com.example.techshop.controller;

import com.example.techshop.domain.User;
import com.example.techshop.service.CartService;
import com.example.techshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService,
                          UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    private Optional<User> getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        String username = auth.getName();
        if (username == null || "anonymousUser".equals(username)) return Optional.empty();
        return userService.findByUsername(username);
    }

    @GetMapping
    public String viewCart(Model model,
                           Authentication auth,
                           HttpSession session) {

        String cartKey = cartService.getCartKey(session);
        var userOpt = getCurrentUser(auth);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Сливаем гостевую корзину в корзину пользователя (один раз)
            cartService.mergeSessionCartIntoUser(cartKey, user);

            var items = cartService.getUserItems(user);
            if (items.isEmpty()) {
                return "cart/empty";
            }
            model.addAttribute("items", items);
            model.addAttribute("total", cartService.getUserTotal(user));
        } else {
            var items = cartService.getSessionItems(cartKey);
            if (items.isEmpty()) {
                return "cart/empty";
            }
            model.addAttribute("items", items);
            model.addAttribute("total", cartService.getSessionTotal(cartKey));
        }

        return "cart/view";
    }

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id,
                            Authentication auth,
                            HttpSession session) {

        String cartKey = cartService.getCartKey(session);
        var userOpt = getCurrentUser(auth);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            cartService.mergeSessionCartIntoUser(cartKey, user);
            cartService.addToUserCart(user, id);
        } else {
            cartService.addToSessionCart(cartKey, id);
        }

        return "redirect:/cart";
    }

    @PostMapping("/removeOne/{id}")
    public String removeOne(@PathVariable Long id,
                            Authentication auth,
                            HttpSession session) {

        String cartKey = cartService.getCartKey(session);
        var userOpt = getCurrentUser(auth);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            cartService.mergeSessionCartIntoUser(cartKey, user);
            cartService.removeOneFromUserCart(user, id);
        } else {
            cartService.removeOneFromSessionCart(cartKey, id);
        }

        return "redirect:/cart";
    }

    @PostMapping("/removeAll/{id}")
    public String removeAll(@PathVariable Long id,
                            Authentication auth,
                            HttpSession session) {

        String cartKey = cartService.getCartKey(session);
        var userOpt = getCurrentUser(auth);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            cartService.mergeSessionCartIntoUser(cartKey, user);
            cartService.removeAllFromUserCart(user, id);
        } else {
            cartService.removeAllFromSessionCart(cartKey, id);
        }

        return "redirect:/cart";
    }
}