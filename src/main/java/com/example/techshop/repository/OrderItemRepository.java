package com.example.techshop.repository;

import com.example.techshop.domain.OrderItem;
import com.example.techshop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsByProduct(Product product);
}