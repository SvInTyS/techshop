package com.example.techshop.controller.admin;

import com.example.techshop.domain.Category;
import com.example.techshop.domain.Product;
import com.example.techshop.service.CategoryService;
import com.example.techshop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public AdminProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("products",
                (q == null || q.isBlank()) ? productService.getAllProducts() : productService.searchByName(q));
        model.addAttribute("q", q);
        return "admin/products/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/products/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.findAll());
        return "admin/products/form";
    }

    // СОЗДАНИЕ НОВОГО ТОВАРА
    @PostMapping("/create")
    public String create(@ModelAttribute("product") Product formProduct) {

        Category category = null;
        if (formProduct.getCategory() != null && formProduct.getCategory().getId() != null) {
            category = categoryService.findById(formProduct.getCategory().getId());
        }
        formProduct.setCategory(category);

        productService.save(formProduct); // тут всегда INSERT
        return "redirect:/admin/products";
    }

    // РЕДАКТИРОВАНИЕ СУЩЕСТВУЮЩЕГО ТОВАРА
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("product") Product formProduct) {

        Product existing = productService.getProductById(id); // берём из БД

        existing.setName(formProduct.getName());
        existing.setDescription(formProduct.getDescription());
        existing.setPrice(formProduct.getPrice());
        existing.setStock(formProduct.getStock());

        Category category = null;
        if (formProduct.getCategory() != null && formProduct.getCategory().getId() != null) {
            category = categoryService.findById(formProduct.getCategory().getId());
        }
        existing.setCategory(category);

        productService.save(existing); // тут ДОЛЖЕН быть UPDATE
        return "redirect:/admin/products";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/admin/products";
    }
}