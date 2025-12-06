package com.example.techshop.service;

import com.example.techshop.domain.User;
import com.example.techshop.dto.ProfileDTO;
import com.example.techshop.dto.UserDTO;
import com.example.techshop.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ===== базовые методы =====

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Создание пользователя из DTO с указанной ролью
     */
    public User createUser(UserDTO dto, String role) {

        // username в DTO = email/логин
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);

        // дополнительные поля профиля
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());

        return userRepository.save(user);
    }

    /**
     * Обновление профиля пользователя из ProfileDTO
     */
    public void updateProfile(User user, ProfileDTO dto) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setPhone(dto.getPhone());
        userRepository.save(user);
    }

    /**
     * Возвращает пользователя или кидает ошибку
     */
    public User getRequiredUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}