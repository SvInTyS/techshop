package com.example.techshop.controller;

import com.example.techshop.service.CartService;
import com.example.techshop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    public CartController(CartService cartService,
                          ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping
    public String viewCart(Model model, HttpSession session) {

        var items = cartService.getItems(session);

        if (items.isEmpty()) {
            return "cart/empty";
        }

        model.addAttribute("items", items);
        model.addAttribute("total", cartService.getTotal(session));

        return "cart/view";
    }

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, HttpSession session) {
        var product = productService.getProductById(id);
        cartService.addToCart(product, session);
        return "redirect:/cart";
    }

    @PostMapping("/removeOne/{id}")
    public String removeOne(@PathVariable Long id, HttpSession session) {
        cartService.removeOne(id, session);
        return "redirect:/cart";
    }

    @PostMapping("/removeAll/{id}")
    public String removeAll(@PathVariable Long id, HttpSession session) {
        cartService.removeAll(id, session);
        return "redirect:/cart";
    }
}