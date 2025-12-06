package com.example.techshop.service;

import com.example.techshop.cart.CartItem;
import com.example.techshop.domain.Product;
import com.example.techshop.domain.User;
import com.example.techshop.domain.UserCartItem;
import com.example.techshop.repository.UserCartItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {

    // session-like key -> in-memory cart (для гостей)
    private final Map<String, List<CartItem>> sessionCarts = new HashMap<>();

    private static final String CART_KEY_ATTR = "CART_KEY";

    private final ProductService productService;
    private final UserCartItemRepository userCartItemRepository;

    public CartService(ProductService productService,
                       UserCartItemRepository userCartItemRepository) {
        this.productService = productService;
        this.userCartItemRepository = userCartItemRepository;
    }

    /**
     * Устойчивый ключ корзины, привязанный к сессии.
     * Хранится в атрибуте сессии, поэтому переживает смену JSESSIONID.
     */
    public String getCartKey(HttpSession session) {
        Object attr = session.getAttribute(CART_KEY_ATTR);
        if (attr != null) {
            return (String) attr;
        }
        String key = UUID.randomUUID().toString();
        session.setAttribute(CART_KEY_ATTR, key);
        return key;
    }

    // ====== SESSION CART (гости) ======

    private List<CartItem> getOrCreateSessionCart(String cartKey) {
        return sessionCarts.computeIfAbsent(cartKey, k -> new ArrayList<>());
    }

    public List<CartItem> getSessionItems(String cartKey) {
        if (cartKey == null) return Collections.emptyList();
        return sessionCarts.getOrDefault(cartKey, new ArrayList<>());
    }

    public BigDecimal getSessionTotal(String cartKey) {
        return getSessionItems(cartKey).stream()
                .map(CartItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addToSessionCart(String cartKey, Long productId) {
        if (cartKey == null) return;
        List<CartItem> items = getOrCreateSessionCart(cartKey);

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

    public void removeOneFromSessionCart(String cartKey, Long productId) {
        if (cartKey == null) return;
        List<CartItem> items = sessionCarts.get(cartKey);
        if (items == null) return;

        Iterator<CartItem> it = items.iterator();
        while (it.hasNext()) {
            CartItem ci = it.next();
            if (ci.getProduct().getId().equals(productId)) {
                int q = ci.getQuantity() - 1;
                if (q <= 0) {
                    it.remove();
                } else {
                    ci.setQuantity(q);
                }
                break;
            }
        }
    }

    public void removeAllFromSessionCart(String cartKey, Long productId) {
        if (cartKey == null) return;
        List<CartItem> items = sessionCarts.get(cartKey);
        if (items != null) {
            items.removeIf(i -> i.getProduct().getId().equals(productId));
        }
    }

    public void clearSessionCart(String cartKey) {
        if (cartKey == null) return;
        sessionCarts.remove(cartKey);
    }

    // ====== USER CART (персистентная, в БД) ======

    public List<CartItem> getUserItems(User user) {
        if (user == null) return Collections.emptyList();
        return userCartItemRepository.findByUser(user).stream()
                .map(e -> new CartItem(e.getProduct(), e.getQuantity()))
                .collect(Collectors.toList());
    }

    public BigDecimal getUserTotal(User user) {
        return getUserItems(user).stream()
                .map(CartItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addToUserCart(User user, Long productId) {
        if (user == null) return;

        Product product = productService.getProductById(productId);
        var existingOpt = userCartItemRepository.findByUserAndProduct(user, product);

        if (existingOpt.isPresent()) {
            UserCartItem e = existingOpt.get();
            e.setQuantity(e.getQuantity() + 1);
            userCartItemRepository.save(e);
        } else {
            UserCartItem e = new UserCartItem(user, product, 1);
            userCartItemRepository.save(e);
        }
    }

    public void removeOneFromUserCart(User user, Long productId) {
        if (user == null) return;

        Product product = productService.getProductById(productId);
        var existingOpt = userCartItemRepository.findByUserAndProduct(user, product);
        if (existingOpt.isEmpty()) return;

        UserCartItem e = existingOpt.get();
        int q = e.getQuantity() - 1;
        if (q <= 0) {
            userCartItemRepository.delete(e);
        } else {
            e.setQuantity(q);
            userCartItemRepository.save(e);
        }
    }

    public void removeAllFromUserCart(User user, Long productId) {
        if (user == null) return;

        Product product = productService.getProductById(productId);
        userCartItemRepository.findByUserAndProduct(user, product)
                .ifPresent(userCartItemRepository::delete);
    }

    /**
     *  перепишем clearUserCart на безопасный вариант с deleteAll
     *  который уже помечен @Transactional внутри Spring Data
     */
    public void clearUserCart(User user) {
        if (user == null) return;
        var items = userCartItemRepository.findByUser(user);
        userCartItemRepository.deleteAll(items);
    }

    /**
     * Переносит позиции из сессионной корзины в корзину пользователя и очищает сессию.
     */
    public void mergeSessionCartIntoUser(String cartKey, User user) {
        if (cartKey == null || user == null) return;

        List<CartItem> sessionItems = sessionCarts.remove(cartKey);
        if (sessionItems == null || sessionItems.isEmpty()) {
            return;
        }

        for (CartItem ci : sessionItems) {
            Product product = productService.getProductById(ci.getProduct().getId());
            var existingOpt = userCartItemRepository.findByUserAndProduct(user, product);
            if (existingOpt.isPresent()) {
                UserCartItem e = existingOpt.get();
                e.setQuantity(e.getQuantity() + ci.getQuantity());
                userCartItemRepository.save(e);
            } else {
                UserCartItem e = new UserCartItem(user, product, ci.getQuantity());
                userCartItemRepository.save(e);
            }
        }
    }
}