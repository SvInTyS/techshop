package com.example.techshop.repository;

import com.example.techshop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Поиск по подстроке в названии (без учёта регистра)
    List<Product> findByNameContainingIgnoreCase(String name);
}