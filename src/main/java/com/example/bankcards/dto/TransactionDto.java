package com.example.bankcards.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDto {

    private Long senderCardId;

    private Long receivedCardId;

    private BigDecimal amount;
}
