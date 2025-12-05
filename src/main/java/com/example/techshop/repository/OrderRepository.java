package com.example.techshop.repository;

import com.example.techshop.domain.Order;
import com.example.techshop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    Optional<Order> findByIdAndUser(Long id, User user);

    // Для админки — все заказы в обратном хронологическом порядке
    List<Order> findAllByOrderByCreatedAtDesc();
}