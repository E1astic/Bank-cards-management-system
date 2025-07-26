package com.example.bankcards.dto.card;

import com.example.bankcards.util.enums.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardAdminDto {

    private Long id;

    private String number;

    private LocalDate activationDate;

    private LocalDate expirationDate;

    private CardStatus status;

    private Long ownerId;

    private BigDecimal balance;
}
