package com.example.techshop.service;

import com.example.techshop.cart.CartItem;
import com.example.techshop.domain.Order;
import com.example.techshop.domain.OrderItem;
import com.example.techshop.domain.Product;
import com.example.techshop.domain.User;
import com.example.techshop.dto.OrderDTO;
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
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository,
                        ProductService productService,
                        CartService cartService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.cartService = cartService;
    }

    /**
     * Создаёт заказ, используя OrderDTO (внутри берёт элементы из cartService для user).
     */
    @Transactional
    public Order createOrder(User user, OrderDTO dto) {
        List<CartItem> cartItems = cartService.getItems(user);

        Order order = new Order();
        order.setUser(user);
        order.setCustomerName(dto.getName()); // <- использует имя из DTO
        order.setPhone(dto.getPhone());
        order.setAddress(dto.getAddress());
        order.setComment(dto.getComment());
        order.setCreatedAt(LocalDateTime.now());

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

    /**
     * Совместимый метод — создаёт заказ из переданных cartItems и переданных полей (используется контроллером, который вызывает createOrderFromCart).
     * Этот метод нужен, если контроллер уже собирает items и прокидывает поля отдельно.
     */
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

    // Остальные вспомогательные методы (поиск истории и т.д.)
    public List<Order> findOrdersForUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Order findByIdAndUser(Long id, User user) {
        return orderRepository.findByIdAndUser(id, user).orElse(null);
    }
}