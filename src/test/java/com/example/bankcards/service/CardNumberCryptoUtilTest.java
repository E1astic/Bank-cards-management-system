package com.example.bankcards.service;

import com.example.bankcards.exception.card.IncorrectCardNumberException;
import com.example.bankcards.util.crypto.CardNumberCryptoUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("Юнит-тесты класса CardNumberCryptoUtil")
public class CardNumberCryptoUtilTest {

    private final CardNumberCryptoUtil cardNumberCryptoUtil = new CardNumberCryptoUtil();

    @Test
    @DisplayName("Метод maskCardNumber: успешное маскирование номера")
    void maskCurdNumber_shouldReturnMaskNumber() {
        String cardNumber = "1111 2222 3333 4444";
        String expectedCardNumber = "**** **** **** 4444";
        String actualCardNumber = cardNumberCryptoUtil.maskCardNumber(cardNumber);

        assertEquals(expectedCardNumber, actualCardNumber);
    }

    @Test
    @DisplayName("Метод maskCardNumber: некорректный номер карты #1")
    void maskCurdNumber_shouldThrowIncorrectCardNumberException1() {
        String cardNumber = "1111 2222 333 4444";

        assertThrows(IncorrectCardNumberException.class, () -> cardNumberCryptoUtil.maskCardNumber(cardNumber));
    }

    @Test
    @DisplayName("Метод maskCardNumber: некорректный номер карты #2")
    void maskCurdNumber_shouldThrowIncorrectCardNumberException2() {
        String cardNumber = "1111222233334444";

        assertThrows(IncorrectCardNumberException.class, () -> cardNumberCryptoUtil.maskCardNumber(cardNumber));
    }
}
