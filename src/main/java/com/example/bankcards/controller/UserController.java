package com.example.bankcards.controller;

import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.dto.UserDataChangeRequest;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/test")
    public String test(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return "Привет, id = " + customUserDetails.getUserId() + ", " + customUserDetails.getUsername();
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<SimpleResponseBody> updateUserInfo(
            @PathVariable Long id, @RequestBody UserDataChangeRequest userDataChangeRequest) {
        userService.updateUser(id, userDataChangeRequest);
        return ResponseEntity.ok(new SimpleResponseBody("Данные пользователя успешно обновлены"));
    }
}
