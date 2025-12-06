package com.example.techshop.controller;

import com.example.techshop.service.CategoryService;
import com.example.techshop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService,
                             CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listProducts(@RequestParam(value = "categoryId", required = false) Long categoryId,
                               Model model) {

        if (categoryId != null) {
            model.addAttribute("products", productService.findByCategoryId(categoryId));
            model.addAttribute("activeCategoryId", categoryId);
        } else {
            model.addAttribute("products", productService.getAllProducts());
        }

        model.addAttribute("categories", categoryService.findAll());

        return "products/list";
    }

    @GetMapping("/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "products/details";
    }
}