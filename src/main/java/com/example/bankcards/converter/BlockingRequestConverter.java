package com.example.bankcards.converter;

import com.example.bankcards.dto.card.CardBlockingResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockingRequest;
import com.example.bankcards.util.enums.BlockingRequestStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BlockingRequestConverter {

    public CardBlockingRequest mapToCardBlockingRequest(Card card) {
        return CardBlockingRequest.builder()
                .card(card)
                .status(BlockingRequestStatus.NOT_COMPLETED)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public CardBlockingResponseDto mapToCardBlockingResponseDto(CardBlockingRequest cardBlockingRequest) {
        return CardBlockingResponseDto.builder()
                .cardId(cardBlockingRequest.getCard() == null ? null : cardBlockingRequest.getCard().getId())
                .status(cardBlockingRequest.getStatus())
                .timestamp(cardBlockingRequest.getTimestamp())
                .build();
    }
}
