package com.example.bankcards.dto.card;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransactionDto {

    @NotNull(message = "ID карты отправителя обязателен")
    @Positive(message = "ID карты отправителя должен быть положительным")
    private Long senderCardId;

    @NotNull(message = "ID карты получателя обязателен")
    @Positive(message = "ID карты получателя должен быть положительным")
    private Long receivedCardId;

    @NotNull(message = "Сумма перевода обязательна")
    @Min(value = 1, message = "Сумма перевода не может быть меньше 1")
    @Max(value = 500000, message = "Сумма перевода не может быть больше 500 000")
    private BigDecimal amount;
}
