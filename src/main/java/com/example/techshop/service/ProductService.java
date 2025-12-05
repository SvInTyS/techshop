package com.example.techshop.service;

import com.example.techshop.domain.Product;
import com.example.techshop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Получить все товары
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Получить товар по id
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElse(null); // позже сделаем обработку ошибок
    }

    // CRUD для админки (пока не используется)
    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}