package com.example.bankcards.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequest {

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email должен быть корректным")
    @Size(max = 60, message = "Email не может быть длиннее 60 символов")
    private String email;

    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 8, max = 100, message = "Длина пароля должна быть от 8 до 100 символов")
    private String password;
}
