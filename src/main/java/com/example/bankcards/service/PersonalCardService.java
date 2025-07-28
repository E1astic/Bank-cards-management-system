package com.example.bankcards.service;

import com.example.bankcards.converter.CardConverter;
import com.example.bankcards.dto.card.CardUserDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.enums.CardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonalCardService {

    private final CardRepository cardRepository;
    private final CardConverter cardConverter;

    public List<CardUserDto> getAllUserCards(Long userId, Boolean fullNumber, Integer size, Integer page) {
        PageRequest pageRequest = validateAndGetPageRequest(page, size);
        return cardRepository.findByOwnerId(userId, pageRequest)
                .stream()
                .map(card -> cardConverter.mapToCardUserDto(card, fullNumber))
                .toList();
    }

    public CardUserDto getUserCardById(Long cardId, Long userId, Boolean fullNumber) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException(
                String.format("Карты с ID = %d не существует", cardId)));
        if(card.getOwner() == null || !userId.equals(card.getOwner().getId())) {
            throw new CardNotFoundException(String.format(
                    "Вам не принадлежит карта с ID = %d", cardId));
        }
        return cardConverter.mapToCardUserDto(card, fullNumber);
    }

    public BigDecimal getPersonalCardsTotalBalance(Long userId) {
        return cardRepository.findByOwnerId(userId)
                .stream()
                .filter(card -> card.getStatus() == CardStatus.ACTIVE)
                .map(Card::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPersonalCardBalance(Long cardId, Long userId) {
        CardUserDto card = getUserCardById(cardId, userId, null);
        return card.getBalance();
    }

    private PageRequest validateAndGetPageRequest(int page, int size) {
        size = (size <= 0) ? 1 : size;
        page = (page < 0) ? 0 : page;
        return PageRequest.of(page, size);
    }
}
