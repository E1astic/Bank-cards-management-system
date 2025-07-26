package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardAdminDto;
import com.example.bankcards.dto.card.CardBlockingResponseDto;
import com.example.bankcards.dto.card.CardRegisterRequest;
import com.example.bankcards.dto.card.CardRegisterResponse;
import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.service.BlockingRequestService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final BlockingRequestService blockingRequestService;

    @GetMapping("/all")
    public List<CardAdminDto> getAllCards(
            @RequestParam(value = "fullNumber", required = false) Boolean fullNumber) {
        return cardService.getAllCards(fullNumber);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardAdminDto> getCardById(
            @PathVariable("id") Long id,
            @RequestParam(value = "fullNumber", required = false) Boolean fullNumber) {
        CardAdminDto card = cardService.getCardById(id, fullNumber);
        return ResponseEntity.ok(card);
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
    public ResponseEntity<SimpleResponseBody> checkTodayExpiredCard() {
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

    @DeleteMapping("/{id}/del")
    public ResponseEntity<SimpleResponseBody> deleteCardById(@PathVariable long id) {
        cardService.deleteCardById(id);
        return ResponseEntity.ok(new SimpleResponseBody("Карта была успешно удалена"));
    }

    @GetMapping("/request/block")
    public List<CardBlockingResponseDto> getCardBlockingRequests() {
        return blockingRequestService.getCardBlockingRequests();
    }
}
