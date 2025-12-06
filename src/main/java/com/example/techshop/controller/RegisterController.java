package com.example.techshop.controller;

import com.example.techshop.dto.UserDTO;
import com.example.techshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDto", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("userDto") UserDTO dto,
            BindingResult bindingResult,
            Model model
    ) {
        // 1. Ошибки валидации (имя/фамилия/email/телефон/пароль)
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // 2. Проверка, что такой логин (email) не занят
        if (userService.findByUsername(dto.getUsername()).isPresent()) {
            model.addAttribute("error", "Пользователь с таким логином уже существует");
            return "register";
        }

        // 3. Создание пользователя с ролью ROLE_USER
        userService.createUser(dto, "ROLE_USER");

        // 4. Редирект на страницу логина
        return "redirect:/login";
    }
}