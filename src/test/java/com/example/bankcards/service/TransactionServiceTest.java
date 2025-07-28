package com.example.bankcards.service;

import com.example.bankcards.dto.card.TransactionDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.card.BlockedCardException;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.exception.card.ExpiredCardException;
import com.example.bankcards.exception.card.InsufficientFundsOnCardException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.enums.CardStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Юнит-тесты класса TransactionService")
public class TransactionServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Long ownerId;
    private Card senderCard;
    private Card receivedCard;
    private User owner;
    private TransactionDto transactionDto;

    @BeforeEach
    void initData() {
        ownerId = 1L;
        senderCard = Card.builder()
                .id(1L)
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal(100))
                .build();
        receivedCard = Card.builder()
                .id(2L)
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal(0))
                .build();
        owner = User.builder()
                .id(ownerId)
                .cards(List.of(senderCard, receivedCard))
                .build();
        senderCard.setOwner(owner); receivedCard.setOwner(owner);
        transactionDto = new TransactionDto(
                senderCard.getId(), receivedCard.getId(), new BigDecimal(100));
    }

    @Test
    @DisplayName("Метод doTransfer: корректный перевод с карты на карту")
    void doTransfer_shouldCorrectlyDoTransfer() {
        when(cardRepository.findById(transactionDto.getSenderCardId())).thenReturn(Optional.of(senderCard));
        when(cardRepository.findById(transactionDto.getReceivedCardId())).thenReturn(Optional.of(receivedCard));

        transactionService.doTransfer(transactionDto, ownerId);

        assertEquals(new BigDecimal(0), senderCard.getBalance());
        assertEquals(new BigDecimal(100), receivedCard.getBalance());
    }

    @Test
    @DisplayName("Метод doTransfer: одна из карт заблокирована")
    void doTransfer_shouldThrowBlockedCardException() {
        receivedCard.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(transactionDto.getSenderCardId())).thenReturn(Optional.of(senderCard));
        when(cardRepository.findById(transactionDto.getReceivedCardId())).thenReturn(Optional.of(receivedCard));

        assertThrows(BlockedCardException.class, () -> transactionService.doTransfer(transactionDto, ownerId));
    }

    @Test
    @DisplayName("Метод doTransfer: у одной из карт истек срок действия")
    void doTransfer_shouldThrowExpiredCardException() {
        receivedCard.setStatus(CardStatus.EXPIRED);

        when(cardRepository.findById(transactionDto.getSenderCardId())).thenReturn(Optional.of(senderCard));
        when(cardRepository.findById(transactionDto.getReceivedCardId())).thenReturn(Optional.of(receivedCard));

        assertThrows(ExpiredCardException.class, () -> transactionService.doTransfer(transactionDto, ownerId));
    }

    @Test
    @DisplayName("Метод doTransfer: недостаточно средств для списания")
    void doTransfer_shouldThrowInsufficientFundsOnCardException() {
        senderCard.setBalance(new BigDecimal(99));

        when(cardRepository.findById(transactionDto.getSenderCardId())).thenReturn(Optional.of(senderCard));
        when(cardRepository.findById(transactionDto.getReceivedCardId())).thenReturn(Optional.of(receivedCard));

        assertThrows(InsufficientFundsOnCardException.class,
                () -> transactionService.doTransfer(transactionDto, ownerId));
    }

    @Test
    @DisplayName("Метод doTransfer: одна из карт не принадлежит пользователю")
    void doTransfer_shouldThrowCardNotFoundException1() {
        User otherUser = new User(); otherUser.setId(2L);
        senderCard.setOwner(otherUser);

        when(cardRepository.findById(transactionDto.getSenderCardId())).thenReturn(Optional.of(senderCard));
        when(cardRepository.findById(transactionDto.getReceivedCardId())).thenReturn(Optional.of(receivedCard));

        assertThrows(CardNotFoundException.class,
                () -> transactionService.doTransfer(transactionDto, ownerId));
    }

    @Test
    @DisplayName("Метод doTransfer: указанной карты для перевода не существует")
    void doTransfer_shouldThrowCardNotFoundException2() {
        when(cardRepository.findById(transactionDto.getSenderCardId())).thenReturn(Optional.of(senderCard));
        when(cardRepository.findById(transactionDto.getReceivedCardId())).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> transactionService.doTransfer(transactionDto, ownerId));
    }
}
