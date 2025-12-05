package com.example.techshop.service;

import com.example.techshop.cart.CartItem;
import com.example.techshop.domain.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {

    private static final String SESSION_CART = "CART";

    @SuppressWarnings("unchecked")
    private Map<Long, CartItem> getCart(HttpSession session) {
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute(SESSION_CART);
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute(SESSION_CART, cart);
        }
        return cart;
    }

    public void addToCart(Product product, HttpSession session) {
        Map<Long, CartItem> cart = getCart(session);

        if (cart.containsKey(product.getId())) {
            cart.get(product.getId()).increment();
        } else {
            cart.put(product.getId(), new CartItem(product));
        }
    }

    public void removeOne(Long productId, HttpSession session) {
        Map<Long, CartItem> cart = getCart(session);

        if (cart.containsKey(productId)) {
            CartItem item = cart.get(productId);
            item.decrement();
            if (item.getQuantity() <= 0) {
                cart.remove(productId);
            }
        }
    }

    public void removeAll(Long productId, HttpSession session) {
        Map<Long, CartItem> cart = getCart(session);
        cart.remove(productId);
    }

    public Collection<CartItem> getItems(HttpSession session) {
        return getCart(session).values();
    }

    /**
     * Возвращает общую сумму корзины как BigDecimal
     */
    public BigDecimal getTotal(HttpSession session) {
        return getItems(session).stream()
                .map(CartItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}