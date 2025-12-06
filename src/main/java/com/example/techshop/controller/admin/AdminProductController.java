package com.example.techshop.controller.admin;

import com.example.techshop.domain.Product;
import com.example.techshop.service.CategoryService;
import com.example.techshop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public AdminProductController(ProductService productService,
                                  CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("products",
                q == null || q.isBlank()
                        ? productService.getAllProducts()
                        : productService.searchByName(q));
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

    @PostMapping("/save")
    public String save(@ModelAttribute("product") Product product,
                       RedirectAttributes ra) {

        if (product.getId() != null) {
            // редактирование существующего товара
            Product existing = productService.getProductById(product.getId());
            existing.setName(product.getName());
            existing.setDescription(product.getDescription());
            existing.setPrice(product.getPrice());
            existing.setStock(product.getStock());

            if (product.getCategory() != null && product.getCategory().getId() != null) {
                existing.setCategory(categoryService.findById(product.getCategory().getId()));
            } else {
                existing.setCategory(null);
            }

            productService.save(existing);
            ra.addFlashAttribute("success", "Товар обновлён");
        } else {
            // создание нового товара
            if (product.getCategory() != null && product.getCategory().getId() != null) {
                product.setCategory(categoryService.findById(product.getCategory().getId()));
            } else {
                product.setCategory(null);
            }

            productService.save(product);
            ra.addFlashAttribute("success", "Товар создан");
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            productService.deleteById(id);
            ra.addFlashAttribute("success", "Товар удалён");
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/products";
    }
}