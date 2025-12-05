package com.example.techshop.controller;

import com.example.techshop.dto.UserDTO;
import com.example.techshop.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
            @ModelAttribute("userDto") UserDTO dto,
            Model model
    ) {

        if (userService.findByUsername(dto.getUsername()).isPresent()) {
            model.addAttribute("error", "Пользователь с таким логином уже существует");
            return "register";
        }

        userService.createUser(dto.getUsername(), dto.getPassword(), "ROLE_USER");
        return "redirect:/login";
    }
}