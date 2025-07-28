package com.example.bankcards.controller;

import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.dto.user.UserDataChangeRequest;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "UserController", description = "Контроллер для управления пользователями администратором")
public class UserController {

    private final UserService userService;

    @PutMapping("/{id}/update")
    @Operation(summary = "Обновление данных пользователя")
    public ResponseEntity<SimpleResponseBody> updateUserInfo(
            @Positive(message = "ID пользователя должен быть положительным") @PathVariable Long id,
            @Valid @RequestBody UserDataChangeRequest userDataChangeRequest) {
        userService.updateUser(id, userDataChangeRequest);
        return ResponseEntity.ok(new SimpleResponseBody("Данные пользователя успешно обновлены"));
    }

    @DeleteMapping("/{id}/del")
    @Operation(summary = "Удаление пользователя")
    public ResponseEntity<SimpleResponseBody> deleteUser(
            @Positive(message = "ID пользователя должнен быть положительным") @PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new SimpleResponseBody("Пользователь был успешно удален"));
    }
}
