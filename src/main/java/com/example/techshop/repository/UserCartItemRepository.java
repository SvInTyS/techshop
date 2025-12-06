package com.example.techshop.repository;

import com.example.techshop.domain.User;
import com.example.techshop.domain.Product;
import com.example.techshop.domain.UserCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCartItemRepository extends JpaRepository<UserCartItem, Long> {

    List<UserCartItem> findByUser(User user);

    Optional<UserCartItem> findByUserAndProduct(User user, Product product);
}