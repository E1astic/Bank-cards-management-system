package com.example.bankcards.dto;

import com.example.bankcards.util.enums.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardUserDto {

    private String number;

    private LocalDate activationDate;

    private LocalDate expirationDate;

    private CardStatus status;

    private BigDecimal balance;
}
