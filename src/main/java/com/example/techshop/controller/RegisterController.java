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

        // Уже есть ошибки валидации полей
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Проверим уникальность email (username)
        if (userService.findByUsername(dto.getEmail()).isPresent()) {
            bindingResult.rejectValue(
                    "email",
                    "duplicate",
                    "Пользователь с такой почтой уже существует"
            );
            return "register";
        }

        userService.createUser(dto, "ROLE_USER");
        return "redirect:/login";
    }
}