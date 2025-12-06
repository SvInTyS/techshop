package com.example.techshop.controller;

import com.example.techshop.domain.User;
import com.example.techshop.dto.ProfileDTO;
import com.example.techshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    private Optional<User> getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        String username = auth.getName();
        if (username == null || "anonymousUser".equals(username)) return Optional.empty();
        return userService.findByUsername(username);
    }

    @GetMapping
    public String viewProfile(Model model, Authentication auth) {

        var userOpt = getCurrentUser(auth);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();

        ProfileDTO dto = new ProfileDTO();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getUsername());
        dto.setPhone(user.getPhone());

        model.addAttribute("profile", dto);
        return "profile";
    }

    @PostMapping
    public String updateProfile(@Valid @ModelAttribute("profile") ProfileDTO dto,
                                BindingResult bindingResult,
                                Authentication auth,
                                Model model) {

        var userOpt = getCurrentUser(auth);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();

        if (bindingResult.hasErrors()) {
            return "profile";
        }

        try {
            userService.updateProfile(user, dto);
        } catch (RuntimeException ex) {
            // например, email уже используется
            bindingResult.rejectValue("email", "duplicate", ex.getMessage());
            return "profile";
        }

        // чтобы показать сообщение "успешно сохранено"
        model.addAttribute("success", true);
        return "profile";
    }
}