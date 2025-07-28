package com.example.bankcards.service;

import com.example.bankcards.converter.CardConverter;
import com.example.bankcards.dto.card.CardRegisterRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockingRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.exception.card.IncorrectCardStatusException;
import com.example.bankcards.exception.user.UserNotFoundException;
import com.example.bankcards.repository.BlockingRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.crypto.CardNumberCryptoUtil;
import com.example.bankcards.util.crypto.CardNumberGenerator;
import com.example.bankcards.util.enums.CardStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Юнит-тесты класса CardAdminService")
public class CardAdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BlockingRequestRepository blockingRequestRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardConverter cardConverter;
    @Mock
    private CardNumberCryptoUtil cryptoUtil;
    @Mock
    private CardNumberGenerator cardNumberGenerator;

    @InjectMocks
    private CardAdminService cardAdminService;

    @Test
    @DisplayName("Метод registerCard: успешная регистрация карты")
    void registerCard_shouldRegisterCard() {
        Long ownerId = 1L;
        CardRegisterRequest cardRegisterRequest = new CardRegisterRequest(1, ownerId);
        User user = new User(); user.setId(ownerId);
        Card card = new Card(); card.setId(1L);
        String cardNumber = "1111 2222 3333 4444";
        String encryptedNumber = "encryptedNumber";
        LocalDate activationDate = LocalDate.now();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(cardNumberGenerator.generateNumber()).thenReturn(cardNumber);
        when(cardConverter.mapToCard(cardRegisterRequest, cardNumber, activationDate, user)).thenReturn(card);
        when(cryptoUtil.encrypt(cardNumber)).thenReturn(encryptedNumber);
        when(cardRepository.save(card)).thenReturn(card);

        long actualCardId = cardAdminService.registerCard(cardRegisterRequest);

        assertEquals(1L, actualCardId);
        verify(userRepository).findById(ownerId);
        verify(cardNumberGenerator).generateNumber();
        verify(cardConverter).mapToCard(cardRegisterRequest, cardNumber, activationDate, user);
        verify(cryptoUtil).encrypt(cardNumber);
        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("Метод registerCard: несуществующий ID владельца")
    void registerCard_shouldThrowUserNotFoundException() {
        Long ownerId = 1L;
        CardRegisterRequest cardRegisterRequest = new CardRegisterRequest(1, ownerId);
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cardAdminService.registerCard(cardRegisterRequest));
    }

    @Test
    @DisplayName("Метод changeCardStatus: успешное изменение статуса")
    void changeCardStatus_shouldChangeCardStatus() {
        Long cardId = 1L;
        List<CardBlockingRequest> blockingRequests = List.of(new CardBlockingRequest(), new CardBlockingRequest());

        when(cardRepository.changeCardStatus(cardId, CardStatus.BLOCKED)).thenReturn(1);
        when(blockingRequestRepository.findByCardId(cardId)).thenReturn(blockingRequests);
        when(blockingRequestRepository.setCompletedStatusByRequestIds(any())).thenReturn(2);

        cardAdminService.changeCardStatus(cardId, "blocked");

        verify(cardRepository).changeCardStatus(cardId, CardStatus.BLOCKED);
        verify(blockingRequestRepository).findByCardId(cardId);
        verify(blockingRequestRepository).setCompletedStatusByRequestIds(any());
    }

    @Test
    @DisplayName("Метод changeCardStatus: несуществующий ID карты")
    void changeCardStatus_shouldThrowCardNotFoundException() {
        Long cardId = 1L;

        when(cardRepository.changeCardStatus(cardId, CardStatus.BLOCKED)).thenReturn(0);

        assertThrows(CardNotFoundException.class, () -> cardAdminService.changeCardStatus(cardId, "blocked"));

        verify(cardRepository).changeCardStatus(cardId, CardStatus.BLOCKED);
        verify(blockingRequestRepository, never()).findByCardId(cardId);
        verify(blockingRequestRepository, never()).setCompletedStatusByRequestIds(any());
    }

    @Test
    @DisplayName("Метод changeCardStatus: некорректный статус карты")
    void changeCardStatus_shouldThrowIncorrectCardStatusException() {
        Long cardId = 1L;

        assertThrows(IncorrectCardStatusException.class,
                () -> cardAdminService.changeCardStatus(cardId, "block"));

        verify(cardRepository, never()).changeCardStatus(cardId, CardStatus.BLOCKED);
        verify(blockingRequestRepository, never()).findByCardId(cardId);
        verify(blockingRequestRepository, never()).setCompletedStatusByRequestIds(any());
    }

    @Test
    @DisplayName("Метод deleteCardById: успешное удаление карты и очистка заявок на блокировку этой карты")
    void deleteCardById_shouldDeleteCardAndClearBlockingRequests() {
        Long cardId = 1L;
        Card card = new Card(); card.setId(cardId);
        List<Card> cards = new ArrayList<>(); cards.add(card);
        User owner = new User(); owner.setCards(cards);
        card.setOwner(owner);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        doNothing().when(blockingRequestRepository).clearCardsByCardId(cardId);
        doNothing().when(cardRepository).deleteByIdNative(cardId);

        cardAdminService.deleteCardById(cardId);

        assertEquals(0, card.getOwner().getCards().size());
        verify(cardRepository).findById(cardId);
        verify(blockingRequestRepository).clearCardsByCardId(cardId);
        verify(cardRepository).deleteByIdNative(cardId);
    }

    @Test
    @DisplayName("Метод deleteCardById: несуществующий ID карты")
    void deleteCardById_shouldThrowCardNotFoundException() {
        Long cardId = 1L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardAdminService.deleteCardById(cardId));

        verify(cardRepository).findById(cardId);
        verify(blockingRequestRepository, never()).clearCardsByCardId(cardId);
        verify(cardRepository, never()).deleteByIdNative(cardId);
    }
}
