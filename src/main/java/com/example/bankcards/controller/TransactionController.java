package com.example.bankcards.controller;

import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.dto.card.TransactionDto;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "TransactionController", description = "Контроллер для совершения денежных переводов между картами")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/create")
    @Operation(summary = "Совершение денежного перевода на указанную сумму между своими двумя картами")
    public ResponseEntity<SimpleResponseBody> createTransaction(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody TransactionDto transactionDto) {
        transactionService.doTransfer(transactionDto, customUserDetails.getUserId());
        return ResponseEntity.ok(new SimpleResponseBody("Перевод выполнен успешно"));
    }
}
