package com.example.techshop.service;

import com.example.techshop.domain.Product;
import com.example.techshop.repository.OrderItemRepository;
import com.example.techshop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;
    private final OrderItemRepository orderItemRepository;

    public ProductService(ProductRepository repo,
                          OrderItemRepository orderItemRepository) {
        this.repo = repo;
        this.orderItemRepository = orderItemRepository;
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product getProductById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public Product save(Product p) {
        return repo.save(p);
    }

    /**
     * Удаление товара. Если по товару есть заказанные позиции
     * кидаем IllegalStateException.
     */
    public void deleteById(Long id) {
        Product product = getProductById(id);

        boolean hasOrderItems = orderItemRepository.existsByProduct(product);
        if (hasOrderItems) {
            throw new IllegalStateException(
                    "Нельзя удалить товар \"" + product.getName() + "\", по нему уже есть заказы"
            );
        }

        repo.delete(product);
    }

    // Optional: search
    public List<Product> searchByName(String q) {
        if (q == null || q.isBlank()) return repo.findAll();
        return repo.findByNameContainingIgnoreCase(q);
    }
}