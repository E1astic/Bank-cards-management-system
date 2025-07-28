package com.example.bankcards.service;

import com.example.bankcards.converter.BlockingRequestConverter;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockingRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.exception.card.InactiveCardException;
import com.example.bankcards.repository.BlockingRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.enums.CardStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Юнит-тесты класса BlockingRequestService")
public class BlockingRequestServiceTest {

    @Mock
    private BlockingRequestRepository blockingRequestRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private BlockingRequestConverter blockingRequestConverter;

    @InjectMocks
    private BlockingRequestService blockingRequestService;

    private Long userId;
    private Long cardId;
    private User user;
    private Card card;
    private CardBlockingRequest cardBlockingRequest;

    @BeforeEach
    void init() {
        userId = 1L;
        cardId = 1L;
        user = User.builder()
                .id(userId)
                .build();
        card = Card.builder()
                .id(cardId)
                .status(CardStatus.ACTIVE)
                .owner(user)
                .build();
        cardBlockingRequest = new CardBlockingRequest();
        cardBlockingRequest.setId(1L);
    }

    @Test
    @DisplayName("Метод createRequest: успешное создание заявки на блокировку карты")
    void createRequest_shouldCreateBlockingRequest() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(blockingRequestConverter.mapToCardBlockingRequest(card)).thenReturn(cardBlockingRequest);
        when(blockingRequestRepository.save(cardBlockingRequest)).thenReturn(cardBlockingRequest);

        blockingRequestService.createRequest(userId, cardId);

        verify(cardRepository).findById(cardId);
        verify(blockingRequestConverter).mapToCardBlockingRequest(card);
        verify(blockingRequestRepository).save(cardBlockingRequest);
    }

    @Test
    @DisplayName("Метод createRequest: попытка создания заявки на неактивированную карту карту")
    void createRequest_shouldThrowInactiveCardException() {
        card.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThrows(InactiveCardException.class, () -> blockingRequestService.createRequest(userId, cardId));

        verify(cardRepository).findById(cardId);
        verify(blockingRequestConverter, never()).mapToCardBlockingRequest(card);
        verify(blockingRequestRepository, never()).save(cardBlockingRequest);
    }

    @Test
    @DisplayName("Метод createRequest: попытка создания заявки на чужую карту")
    void createRequest_shouldThrowCardNotFoundException1() {
        User otherUser = new User(); otherUser.setId(2L);
        card.setOwner(otherUser);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThrows(CardNotFoundException.class, () -> blockingRequestService.createRequest(userId, cardId));

        verify(cardRepository).findById(cardId);
        verify(blockingRequestConverter, never()).mapToCardBlockingRequest(card);
        verify(blockingRequestRepository, never()).save(cardBlockingRequest);
    }

    @Test
    @DisplayName("Метод createRequest: попытка создания заявки на несуществующую карту")
    void createRequest_shouldThrowCardNotFoundException2() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> blockingRequestService.createRequest(userId, cardId));

        verify(cardRepository).findById(cardId);
        verify(blockingRequestConverter, never()).mapToCardBlockingRequest(card);
        verify(blockingRequestRepository, never()).save(cardBlockingRequest);
    }
}
