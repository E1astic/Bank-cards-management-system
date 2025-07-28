package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.enums.CardStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Юнит-тесты класса PersonalCardService")
public class PersonalCardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private PersonalCardService personalCardService;

    @Test
    @DisplayName("Метод getPersonalCardsTotalBalance: корректный суммарный баланс всех активированных карт")
    void getPersonalCardsTotalBalance_shouldReturnCorrectlyTotalBalance() {
        Long userId = 1L;
        Card card1 = new Card(); card1.setStatus(CardStatus.ACTIVE); card1.setBalance(new BigDecimal(50));
        Card card2 = new Card(); card2.setStatus(CardStatus.ACTIVE); card2.setBalance(new BigDecimal(100));
        Card card3 = new Card(); card3.setStatus(CardStatus.BLOCKED); card3.setBalance(new BigDecimal(300));
        Card card4 = new Card(); card4.setStatus(CardStatus.EXPIRED); card4.setBalance(new BigDecimal(400));
        List<Card> cards = List.of(card1, card2, card3, card4);

        when(cardRepository.findByOwnerId(userId)).thenReturn(cards);

        BigDecimal totalBalance = personalCardService.getPersonalCardsTotalBalance(userId);

        assertEquals(card1.getBalance().add(card2.getBalance()), totalBalance);
    }
}
