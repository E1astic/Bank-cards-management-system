package com.example.bankcards.controller;

import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.dto.UserLoginRequest;
import com.example.bankcards.dto.UserLoginResponse;
import com.example.bankcards.dto.UserRegisterRequest;
import com.example.bankcards.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<SimpleResponseBody> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        authService.register(userRegisterRequest);
        return ResponseEntity.ok(new SimpleResponseBody("Регистрация прошла успешно. Теперь необходимо залогиниться"));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        UserLoginResponse userLoginResponse = authService.login(userLoginRequest);
        return ResponseEntity.ok(userLoginResponse);
    }
}
