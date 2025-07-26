package com.example.bankcards.controller;

import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.dto.card.BalanceResponseDto;
import com.example.bankcards.dto.card.CardUserDto;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.BlockingRequestService;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
public class PersonalCardController {

    private final CardService cardService;
    private final BlockingRequestService blockingRequestService;

    @GetMapping
    public List<CardUserDto> getAllPersonalCards(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "fullNumber", required = false) Boolean fullNumber) {
        return cardService.getAllUserCards(customUserDetails.getUserId(), fullNumber);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardUserDto> getPersonalCardById(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "fullNumber", required = false) Boolean fullNumber,
            @PathVariable("id") Long id) {
        CardUserDto cardUserDto = cardService.getUserCardById(id, customUserDetails.getUserId(), fullNumber);
        return ResponseEntity.ok(cardUserDto);
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponseDto> getPersonalCardsTotalBalance(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        BigDecimal totalBalance = cardService.getPersonalCardsTotalBalance(customUserDetails.getUserId());
        return ResponseEntity.ok(new BalanceResponseDto(totalBalance));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceResponseDto> getPersonalCardBalance(
            @AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("id") Long id) {
        BigDecimal cardBalance = cardService.getPersonalCardBalance(id, customUserDetails.getUserId());
        return ResponseEntity.ok(new BalanceResponseDto(cardBalance));
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<SimpleResponseBody> createCardBlockingRequest(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("id") Long id) {
        blockingRequestService.createRequest(customUserDetails.getUserId(), id);
        return ResponseEntity.ok(new SimpleResponseBody("Завка на блокировку карты успешно отправлена"));
    }
}
