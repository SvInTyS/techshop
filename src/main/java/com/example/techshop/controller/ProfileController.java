package com.example.techshop.controller;

import com.example.techshop.domain.User;
import com.example.techshop.dto.ProfileDTO;
import com.example.techshop.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    private User getCurrentUserOrThrow(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Not authenticated");
        }
        String username = auth.getName();
        return userService.getRequiredUser(username);
    }

    @GetMapping
    public String showProfile(Model model, Authentication auth) {

        User user = getCurrentUserOrThrow(auth);

        // если после редиректа уже есть profileDto (с ошибкой) — не перезатираем
        if (!model.containsAttribute("profileDto")) {
            ProfileDTO dto = new ProfileDTO();
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setUsername(user.getUsername());
            dto.setPhone(user.getPhone());
            model.addAttribute("profileDto", dto);
        }

        return "profile/edit";
    }

    @PostMapping
    public String updateProfile(
            @ModelAttribute("profileDto") ProfileDTO dto,
            Authentication auth,
            RedirectAttributes ra
    ) {
        User user = getCurrentUserOrThrow(auth);

        // проверяем, что новый username не занят другим пользователем
        var existing = userService.findByUsername(dto.getUsername());
        if (existing.isPresent() && !existing.get().getId().equals(user.getId())) {
            ra.addFlashAttribute("error", "Пользователь с таким логином уже существует");
            ra.addFlashAttribute("profileDto", dto);
            return "redirect:/profile";
        }

        userService.updateProfile(user, dto);

        ra.addFlashAttribute("success", "Профиль обновлён");
        return "redirect:/profile";
    }
}