package com.example.techshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserDTO {

    @NotBlank(message = "Имя обязательно")
    @Pattern(
            regexp = "^[А-Яа-яA-Za-z\\-\\s]{2,30}$",
            message = "Имя должно содержать только буквы и быть от 2 до 30 символов"
    )
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Pattern(
            regexp = "^[А-Яа-яA-Za-z\\-\\s]{2,30}$",
            message = "Фамилия должна содержать только буквы и быть от 2 до 30 символов"
    )
    private String lastName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String username; // используем как логин

    @NotBlank(message = "Телефон обязателен")
    @Pattern(
            regexp = "^\\+?\\d{11,15}$",
            message = "Телефон должен содержать от 11 до 15 цифр, можно с + в начале"
    )
    private String phone;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, max = 100, message = "Пароль должен быть не короче 6 символов")
    private String password;

    public UserDTO() {
    }

    // ---- getters / setters ----

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}