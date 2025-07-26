package com.example.bankcards.service;

import com.example.bankcards.dto.card.TransactionDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.card.BlockedCardException;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.exception.card.ExpiredCardException;
import com.example.bankcards.exception.card.InsufficientFundsOnCardException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.enums.CardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final CardRepository cardRepository;

    @Transactional
    public void doTransfer(TransactionDto transaction, Long userId) {
        Card senderCard = cardRepository.findById(transaction.getSenderCardId()).orElseThrow(
                () -> new CardNotFoundException(String.format(
                        "Карты с ID = %d не существует", transaction.getSenderCardId())));
        Card receivedCard = cardRepository.findById(transaction.getReceivedCardId()).orElseThrow(
                () -> new CardNotFoundException(String.format(
                        "Карты с ID = %d не существует", transaction.getReceivedCardId())));

        checkCardOwnership(senderCard, userId);
        checkCardOwnership(receivedCard, userId);

        checkCardBlock(senderCard);
        checkCardExpiration(senderCard);

        checkCardBlock(receivedCard);
        checkCardExpiration(receivedCard);

        BigDecimal senderCardBalance = senderCard.getBalance();
        BigDecimal amount = transaction.getAmount();
        BigDecimal receivedCardBalance = receivedCard.getBalance();
        if(senderCardBalance.compareTo(amount) >= 0) {
            senderCard.setBalance(senderCardBalance.subtract(amount));
            receivedCard.setBalance(receivedCardBalance.add(amount));
        } else {
            throw new InsufficientFundsOnCardException();
        }
    }

    private void checkCardOwnership(Card card, Long userId) {
        if(card.getOwner() == null || !userId.equals(card.getOwner().getId())) {
            throw new CardNotFoundException(String.format(
                    "Вам не принадлежит карта с ID = %d", card.getId()));
        }
    }

    private void checkCardBlock(Card card) {
        if(card.getStatus() == CardStatus.BLOCKED) {
            throw new BlockedCardException(String.format("Карта с ID = %d заблокирована", card.getId()));
        }
    }

    private void checkCardExpiration(Card card) {
        if(card.getStatus() == CardStatus.EXPIRED) {
            throw new ExpiredCardException(String.format("У карты с ID = %d истек срок действия", card.getId()));
        }
    }
}
