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

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Создание пользователя на основе DTO (регистрация).
     */
    public User createUser(UserDTO dto, String role) {

        if (userRepository.findByUsername(dto.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUsername(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());

        return userRepository.save(user);
    }

    /**
     * Обновление профиля текущего пользователя.
     */
    public User updateProfile(User user, ProfileDTO dto) {

        // если email меняется — проверяем уникальность
        String newEmail = dto.getEmail();
        if (!user.getUsername().equals(newEmail)) {
            userRepository.findByUsername(newEmail).ifPresent(existing -> {
                if (!existing.getId().equals(user.getId())) {
                    throw new RuntimeException("Email already in use");
                }
            });
        }

        user.setUsername(newEmail);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());

        return userRepository.save(user);
    }

    /**
     * Возвращает пользователя или кидает ошибку
     */
    public User getRequiredUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}
