package com.example.techshop.service;

import com.example.techshop.cart.CartItem;
import com.example.techshop.domain.Product;
import com.example.techshop.domain.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CartService {

    // userId → list of cart items
    private final Map<Long, List<CartItem>> cartData = new HashMap<>();

    private final ProductService productService;

    public CartService(ProductService productService) {
        this.productService = productService;
    }

    public List<CartItem> getItems(User user) {
        return cartData.getOrDefault(user.getId(), new ArrayList<>());
    }

    public BigDecimal getTotal(User user) {
        return getItems(user).stream()
                .map(CartItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addToCart(User user, Long productId) {
        List<CartItem> items = cartData.computeIfAbsent(user.getId(), k -> new ArrayList<>());

        Optional<CartItem> existing = items.stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + 1);
        } else {
            Product p = productService.getProductById(productId);
            items.add(new CartItem(p));
        }
    }

    public void removeOne(User user, Long productId) {
        List<CartItem> items = cartData.get(user.getId());
        if (items == null) return;

        items.removeIf(i -> {
            if (i.getProduct().getId().equals(productId)) {
                int q = i.getQuantity() - 1;
                if (q <= 0) return true;
                i.setQuantity(q);
            }
            return false;
        });
    }

    public void removeAll(User user, Long productId) {
        List<CartItem> items = cartData.get(user.getId());
        if (items != null) {
            items.removeIf(i -> i.getProduct().getId().equals(productId));
        }
    }

    public void clear(User user) {
        cartData.remove(user.getId());
    }
}