package com.example.bankcards.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceResponseDto {

    private BigDecimal balance;
}
