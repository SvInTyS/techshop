package com.example.techshop.config;

import com.example.techshop.domain.User;
import com.example.techshop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            if (userRepo.findByUsername("admin") == null) {
                User admin = new User("admin", encoder.encode("admin"), "ROLE_ADMIN");
                userRepo.save(admin);
                System.out.println("Admin user created: admin / admin");
            }
        };
    }
}
