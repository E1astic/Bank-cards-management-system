package com.example.bankcards.service;

import com.example.bankcards.converter.BlockingRequestConverter;
import com.example.bankcards.dto.card.CardBlockingResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockingRequest;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.exception.card.InactiveCardException;
import com.example.bankcards.repository.BlockingRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.enums.CardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockingRequestService {

    private final BlockingRequestRepository blockingRequestRepository;
    private final CardRepository cardRepository;
    private final BlockingRequestConverter blockingRequestConverter;

    @Transactional
    public void createRequest(Long userId, Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException(
                String.format("Карты с ID = %d не существует", cardId)));
        if(card.getOwner() == null || !userId.equals(card.getOwner().getId())) {
            throw new CardNotFoundException(String.format(
                    "Вам не принадлежит карта с ID = %d", cardId));
        }
        if(card.getStatus() == CardStatus.ACTIVE) {
            CardBlockingRequest cardBlockingRequest = blockingRequestConverter.mapToCardBlockingRequest(card);
            blockingRequestRepository.save(cardBlockingRequest);
        } else {
            throw new InactiveCardException(String.format(
                    "Карта с ID = %d уже заблокирована или у нее истек срок действия", cardId));
        }
    }

    public List<CardBlockingResponseDto> getCardBlockingRequests() {
        return blockingRequestRepository.findAll()
                .stream()
                .map(blockingRequestConverter::mapToCardBlockingResponseDto)
                .toList();
    }
}
