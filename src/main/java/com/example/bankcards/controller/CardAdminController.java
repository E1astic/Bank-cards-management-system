package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardAdminDto;
import com.example.bankcards.dto.card.CardBlockingResponseDto;
import com.example.bankcards.dto.card.CardRegisterRequest;
import com.example.bankcards.dto.card.CardRegisterResponse;
import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.service.BlockingRequestService;
import com.example.bankcards.service.CardAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
@Validated
@Tag(name = "CardAdminController", description = "Контроллер для управления картами администратором")
public class CardAdminController {

    private final CardAdminService cardAdminService;
    private final BlockingRequestService blockingRequestService;

    @GetMapping("/all")
    @Operation(summary = "Получение всех карт с возможностью постраничной выдачи")
    public List<CardAdminDto> getAllCards(
            @RequestParam(value = "fullNumber", required = false) Boolean fullNumber,
            @Positive(message = "Количетво элементов на странице должно быть положительным")
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Min(value = 0, message = "Номер страницы не может быть отрицательным")
            @RequestParam(value = "page", defaultValue = "0") Integer page) {
        return cardAdminService.getAllCards(fullNumber, size, page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение карты по ID")
    public ResponseEntity<CardAdminDto> getCardById(
            @Positive(message = "ID карты должен быть положительным") @PathVariable("id") Long id,
            @RequestParam(value = "fullNumber", required = false) Boolean fullNumber) {
        CardAdminDto card = cardAdminService.getCardById(id, fullNumber);
        return ResponseEntity.ok(card);
    }

    @PostMapping("/register")
    @Operation(summary = "Создание новой карты для пользователя")
    public ResponseEntity<CardRegisterResponse> registerCard(
            @Valid @RequestBody CardRegisterRequest cardRegisterRequest) {
        long cardId = cardAdminService.registerCard(cardRegisterRequest);
        return ResponseEntity.ok(new CardRegisterResponse(cardId));
    }

    @PatchMapping("{id}/status/{status}")
    @Operation(summary = "Изменение статуса карты по ID (активация, блокировка, истечение срока действия)")
    public ResponseEntity<SimpleResponseBody> updateCardStatus(
            @Positive(message = "ID карты должен быть положительным") @PathVariable("id") Long id,
            @PathVariable("status") String status) {
        cardAdminService.changeCardStatus(id, status);
        return ResponseEntity.ok(new SimpleResponseBody("Статус карты был успешен изменен"));
    }

    @PatchMapping("/expired/today/check")
    @Operation(summary = "Автоматическая установка статуса EXPIRED для всех карт, " +
            "срок которых истекает в текущий день")
    public ResponseEntity<SimpleResponseBody> checkTodayExpiredCard() {
        int expiredCards = cardAdminService.updateExpiredCards(LocalDate.now());
        return ResponseEntity.ok(new SimpleResponseBody(String.format(
                "У %d карт сегодня истекает срок. Их статус был успешен изменен", expiredCards)));
    }

    @PatchMapping("/expired/{expirationDate}/check")
    @Operation(summary = "Автоматическая установка статуса EXPIRED для всех карт, " +
            "срок которых истекает в указанный прошедший или текущий день")
    public ResponseEntity<SimpleResponseBody> checkExpiredCard(
            @PastOrPresent(message = "Дата должна быть прошедшей или текущей")
            @PathVariable("expirationDate") LocalDate expirationDate) {
        int expiredCards = cardAdminService.updateExpiredCards(expirationDate);
        return ResponseEntity.ok(new SimpleResponseBody(String.format(
                "У %d карт %s истекает срок. Их статус был успешен изменен", expiredCards, expirationDate)));
    }

    @DeleteMapping("/{id}/del")
    @Operation(summary = "Удаление карты по ID")
    public ResponseEntity<SimpleResponseBody> deleteCardById(
            @Positive(message = "ID карты должен быть положительным") @PathVariable long id) {
        cardAdminService.deleteCardById(id);
        return ResponseEntity.ok(new SimpleResponseBody("Карта была успешно удалена"));
    }

    @GetMapping("/request/block")
    @Operation(summary = "Получение всех заявок пользователей на блокировку карты")
    public List<CardBlockingResponseDto> getCardBlockingRequests() {
        return blockingRequestService.getCardBlockingRequests();
    }
}
