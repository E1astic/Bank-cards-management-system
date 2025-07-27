package com.example.bankcards.dto.card;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CardRegisterRequest {

    @NotNull(message = "Срок действия карты обязателен")
    @Min(value = 1, message = "Срок действия карты должен быть минимум 1 год")
    @Max(value = 10, message = "Срок действия карты должен быть максимум 10 лет")
    private Integer expirationYears;

    @NotNull(message = "Владелец карты обязателен")
    @Positive(message = "ID владельца карты должно быть положительным")
    private Long ownerId;
}
