package com.example.bankcards.dto.user;

import lombok.Data;

@Data
public class UserDataChangeRequest {

    private String password;

    private String phone;

    private String surname;

    private String name;

    private String patronymic;
}
