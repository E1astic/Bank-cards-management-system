package com.example.bankcards.controller;

import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.dto.card.TransactionDto;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<SimpleResponseBody> createTransaction(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody TransactionDto transactionDto) {
        transactionService.doTransfer(transactionDto, customUserDetails.getUserId());
        return ResponseEntity.ok(new SimpleResponseBody("Перевод выполнен успешно"));
    }
}
