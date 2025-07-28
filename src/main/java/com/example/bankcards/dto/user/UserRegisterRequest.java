package com.example.bankcards.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegisterRequest {

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email должен быть корректным")
    @Size(max = 60, message = "Email не может быть длиннее 60 символов")
    private String email;

    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 8, max = 100, message = "Длина пароля должна быть от 8 до 100 символов")
    private String password;

    @NotBlank(message = "Номер телефона не должен быть пустым")
    @Pattern(regexp = "^\\+?[0-9]{11,12}$", message = "Неверный формат телефона")
    private String phone;

    @NotBlank(message = "Фамилия не должна быть пустой")
    @Size(min = 2, max = 60, message = "Длина фамилии должна быть от 2 до 60 символов")
    private String surname;

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 60, message = "Длина имени должна быть от 2 до 60 символов")
    private String name;

    @NotBlank(message = "Отчество не должно быть пустым")
    @Size(min = 2, max = 60, message = "Длина отчества должна быть от 2 до 60 символов")
    private String patronymic;
}
