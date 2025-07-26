package com.example.bankcards.dto.user;

import lombok.Data;

@Data
public class UserRegisterRequest {

    private String email;

    private String password;

    private String phone;

    private String surname;

    private String name;

    private String patronymic;
}
