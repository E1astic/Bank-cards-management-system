package com.example.bankcards.controller;

import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.dto.user.UserDataChangeRequest;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PutMapping("/{id}/update")
    public ResponseEntity<SimpleResponseBody> updateUserInfo(
            @PathVariable Long id, @RequestBody UserDataChangeRequest userDataChangeRequest) {
        userService.updateUser(id, userDataChangeRequest);
        return ResponseEntity.ok(new SimpleResponseBody("Данные пользователя успешно обновлены"));
    }

    @DeleteMapping("/{id}/del")
    public ResponseEntity<SimpleResponseBody> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new SimpleResponseBody("Пользователь был успешно удален"));
    }
}
