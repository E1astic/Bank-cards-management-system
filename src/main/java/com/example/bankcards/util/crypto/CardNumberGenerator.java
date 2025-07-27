package com.example.bankcards.util.crypto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class CardNumberGenerator {

    private final CardNumberCryptoUtil cryptoUtil;
    private final CardRepository cardRepository;

    public String generateNumber() {
        final int digitsNumber = 16;
        final int spacesNumber = 3;
        final int numberLength = digitsNumber + spacesNumber;

        boolean replay = true;
        StringBuilder cardNumber = new StringBuilder();

        while(replay) {
            SecureRandom secureRandom = new SecureRandom();
            for (int i = 0; i < numberLength; ++i) {
                if ((i + 1) % 5 == 0) {
                    cardNumber.append(" ");
                } else {
                    cardNumber.append(secureRandom.nextInt(10));
                }
            }
            Card card = cardRepository.findByNumber(cryptoUtil.encrypt(cardNumber.toString())).orElse(null);
            if(card == null) {
                replay = false;
            } else {
                cardNumber.setLength(0);
            }
        }
        return cardNumber.toString();
    }

}
