package com.example.bankcards.controller;

import com.example.bankcards.dto.CardAdminDto;
import com.example.bankcards.dto.CardRegisterRequest;
import com.example.bankcards.dto.CardRegisterResponse;
import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/all")
    public List<CardAdminDto> getAllCards() {
        return cardService.getAllCards();
    }

    @PostMapping("/register")
    public ResponseEntity<CardRegisterResponse> registerCard(@RequestBody CardRegisterRequest cardRegisterRequest) {
        long cardId = cardService.registerCard(cardRegisterRequest);
        return ResponseEntity.ok(new CardRegisterResponse(cardId));
    }

    @PatchMapping("{id}/status/{status}")
    public ResponseEntity<SimpleResponseBody> updateCardStatus(
            @PathVariable("id") Long id, @PathVariable("status") String status) {
        cardService.changeCardStatus(id, status);
        return ResponseEntity.ok(new SimpleResponseBody("Статус карты был успешно изменен"));
    }

    @PatchMapping("/expired/today/check")
    public ResponseEntity<SimpleResponseBody> checkExpiredCard() {
        int expiredCards = cardService.updateExpiredCards(LocalDate.now().toString());
        return ResponseEntity.ok(new SimpleResponseBody(String.format(
                "У %d карт сегодня истекает срок. Их статус был успешно изменен", expiredCards)));
    }

    @PatchMapping("/expired/{expirationDate}/check")
    public ResponseEntity<SimpleResponseBody> checkExpiredCard(
            @PathVariable("expirationDate") String expirationDate) {
        int expiredCards = cardService.updateExpiredCards(expirationDate);
        return ResponseEntity.ok(new SimpleResponseBody(String.format(
                "У %d карт %s истекает срок. Их статус был успешно изменен", expiredCards, expirationDate)));
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<SimpleResponseBody> deleteCardById(@PathVariable long id) {
        cardService.deleteCardById(id);
        return ResponseEntity.ok(new SimpleResponseBody("Карта была успешно удалена"));
    }
}
