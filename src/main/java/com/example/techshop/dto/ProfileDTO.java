package com.example.techshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProfileDTO {

    @NotBlank(message = "Имя обязательно")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\-]{2,50}$",
            message = "Имя должно содержать только буквы и быть длиной от 2 до 50 символов")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\-]{2,50}$",
            message = "Фамилия должна содержать только буквы и быть длиной от 2 до 50 символов")
    private String lastName;

    @NotBlank(message = "Почта обязательна")
    @Email(message = "Введите корректный email")
    private String email;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(
            regexp = "^\\+?\\d{11}$",
            message = "Телефон должен содержать 11 цифр (например, +79991234567)"
    )
    private String phone;

    public ProfileDTO() {
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}