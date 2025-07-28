package com.example.bankcards.controller;

import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.dto.card.BalanceResponseDto;
import com.example.bankcards.dto.card.CardUserDto;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.BlockingRequestService;
import com.example.bankcards.service.PersonalCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/card/my")
@RequiredArgsConstructor
@Validated
@Tag(name = "PersonalCardController", description =
        "Контроллер для управления персональными картами каждым пользователем")
public class PersonalCardController {

    private final PersonalCardService personalCardService;
    private final BlockingRequestService blockingRequestService;

    @GetMapping
    @Operation(summary = "Получение всех карт текущего пользователя с возможностью постраничной выдачи")
    public List<CardUserDto> getAllPersonalCards(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "fullNumber", required = false) Boolean fullNumber,
            @Positive(message = "Количетво элементов на странице должно быть положительным")
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Min(value = 0, message = "Номер страницы не может быть отрицательным")
            @RequestParam(value = "page", defaultValue = "0") Integer page) {
        return personalCardService.getAllUserCards(customUserDetails.getUserId(), fullNumber, size, page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение карты пользователя по ID")
    public ResponseEntity<CardUserDto> getPersonalCardById(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "fullNumber", required = false) Boolean fullNumber,
            @Positive(message = "ID карты должен быть положительным") @PathVariable("id") Long id) {
        CardUserDto cardUserDto = personalCardService.getUserCardById(id, customUserDetails.getUserId(), fullNumber);
        return ResponseEntity.ok(cardUserDto);
    }

    @GetMapping("/balance")
    @Operation(summary = "Получение суммарного баланса всех активированных карт пользователя")
    public ResponseEntity<BalanceResponseDto> getPersonalCardsTotalBalance(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        BigDecimal totalBalance = personalCardService.getPersonalCardsTotalBalance(customUserDetails.getUserId());
        return ResponseEntity.ok(new BalanceResponseDto(totalBalance));
    }

    @GetMapping("/{id}/balance")
    @Operation(summary = "Получение баланса карты пользователя по ID")
    public ResponseEntity<BalanceResponseDto> getPersonalCardBalance(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Positive(message = "ID карты должен быть положительным") @PathVariable("id") Long id) {
        BigDecimal cardBalance = personalCardService.getPersonalCardBalance(id, customUserDetails.getUserId());
        return ResponseEntity.ok(new BalanceResponseDto(cardBalance));
    }

    @PostMapping("/{id}/block")
    @Operation(summary = "Создание запроса на блокировку карты с переданным ID")
    public ResponseEntity<SimpleResponseBody> createCardBlockingRequest(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Positive(message = "ID карты должен быть положительным") @PathVariable("id") Long id) {
        blockingRequestService.createRequest(customUserDetails.getUserId(), id);
        return ResponseEntity.ok(new SimpleResponseBody("Завка на блокировку карты успешно отправлена"));
    }
}
