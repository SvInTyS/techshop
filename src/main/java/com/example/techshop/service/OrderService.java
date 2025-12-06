package com.example.techshop.service;

import com.example.techshop.cart.CartItem;
import com.example.techshop.domain.Order;
import com.example.techshop.domain.OrderItem;
import com.example.techshop.domain.OrderStatus;
import com.example.techshop.domain.Product;
import com.example.techshop.domain.User;
import com.example.techshop.repository.OrderRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    @Transactional
    public Order createOrderFromCart(User user,
                                     List<CartItem> cartItems,
                                     String name,
                                     String phone,
                                     String address,
                                     String comment) {

        Order order = new Order();
        order.setUser(user);
        order.setCustomerName(name);
        order.setPhone(phone);
        order.setAddress(address);
        order.setComment(comment);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);

        List<OrderItem> items = cartItems.stream().map(ci -> {
            Product p = productService.getProductById(ci.getProduct().getId());
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setPrice(p.getPrice());
            oi.setQuantity(ci.getQuantity());
            return oi;
        }).collect(Collectors.toList());

        order.setItems(items);

        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(total);

        return orderRepository.save(order);
    }

    public List<Order> findOrdersForUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Order findByIdAndUser(Long id, User user) {
        return orderRepository.findByIdAndUser(id, user).orElse(null);
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }
}