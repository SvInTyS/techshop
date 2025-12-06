package com.example.techshop.controller.admin;

import com.example.techshop.domain.Category;
import com.example.techshop.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(@RequestParam(value="q", required=false) String q, Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("q", q);
        return "admin/categories/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/categories/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.findById(id));
        return "admin/categories/form";
    }

    // СОЗДАНИЕ НОВОЙ КАТЕГОРИИ
    @PostMapping("/create")
    public String create(@ModelAttribute("category") Category formCategory) {
        // formCategory.id == null -> точно новая
        categoryService.save(formCategory);
        return "redirect:/admin/categories";
    }

    // РЕДАКТИРОВАНИЕ СУЩЕСТВУЮЩЕЙ КАТЕГОРИИ
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("category") Category formCategory) {

        Category existing = categoryService.findById(id);
        if (existing != null) {
            existing.setName(formCategory.getName());
            categoryService.save(existing);  // тут должен быть UPDATE
        }

        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return "redirect:/admin/categories";
    }
}