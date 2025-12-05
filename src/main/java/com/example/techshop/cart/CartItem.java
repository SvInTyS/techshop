package com.example.techshop.cart;

import com.example.techshop.domain.Product;

import java.math.BigDecimal;

public class CartItem {

    private final Product product;
    private int quantity;

    public CartItem(Product product) {
        this.product = product;
        this.quantity = 1;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increment() {
        this.quantity++;
    }

    public void decrement() {
        this.quantity--;
    }

    /**
     * Возвращает стоимость позиции как BigDecimal: price * quantity
     */
    public BigDecimal getTotal() {
        BigDecimal price = product.getPrice();
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}