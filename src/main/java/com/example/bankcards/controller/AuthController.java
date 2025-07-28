package com.example.bankcards.controller;

import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.dto.user.UserLoginRequest;
import com.example.bankcards.dto.user.UserLoginResponse;
import com.example.bankcards.dto.user.UserRegisterRequest;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "Контроллер для регистрации и логина пользователей")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя")
    public ResponseEntity<SimpleResponseBody> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        authService.register(userRegisterRequest);
        return ResponseEntity.ok(new SimpleResponseBody("Регистрация прошла успешно. Теперь необходимо залогиниться"));
    }

    @PostMapping("/login")
    @Operation(summary = "Вход пользователя в систему")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        String jwt = authService.login(userLoginRequest);
        return ResponseEntity.ok(new UserLoginResponse(jwt));
    }
}
