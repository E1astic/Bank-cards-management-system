package com.example.bankcards.dto.card;

import lombok.Data;

@Data
public class CardRegisterRequest {

    private Integer expirationYears;

    private Long ownerId;
}
