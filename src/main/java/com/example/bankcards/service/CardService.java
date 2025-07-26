package com.example.bankcards.service;

import com.example.bankcards.converter.CardConverter;
import com.example.bankcards.dto.card.CardAdminDto;
import com.example.bankcards.dto.card.CardRegisterRequest;
import com.example.bankcards.dto.card.CardUserDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockingRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.exception.card.IncorrectCardStatusException;
import com.example.bankcards.exception.user.UserNotFoundException;
import com.example.bankcards.repository.BlockingRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.crypto.CryptoUtil;
import com.example.bankcards.util.enums.CardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardConverter cardConverter;
    private final CryptoUtil cryptoUtil;
    private final BlockingRequestRepository blockingRequestRepository;

    public List<CardAdminDto> getAllCards(Boolean fullNumber) {
        return cardRepository.findAll()
                .stream()
                .map(card -> cardConverter.mapToCardAdminDto(card, fullNumber))
                .toList();
    }

    public CardAdminDto getCardById(Long id, Boolean fullNumber) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new CardNotFoundException(String.format("Карты с ID = %d не существует", id)));
        return cardConverter.mapToCardAdminDto(card, fullNumber);
    }

    @Transactional
    public long registerCard(CardRegisterRequest cardRegisterRequest) {
        User owner = userRepository.findById(cardRegisterRequest.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException(String.format(
                        "Пользователя с ID = %d не существует", cardRegisterRequest.getOwnerId())));
        Card card = cardConverter.mapToCard(cardRegisterRequest, LocalDate.now(), owner);
        card.setNumber(cryptoUtil.encrypt(cardRegisterRequest.getNumber()));
        card = cardRepository.save(card);
        return card.getId();
    }

    @Transactional
    public void changeCardStatus(long id, String newStatus) {
        CardStatus cardStatus;
        try {
            cardStatus = CardStatus.valueOf(newStatus.toUpperCase());
        } catch(IllegalArgumentException e) {
            throw new IncorrectCardStatusException();
        }
        int rowsUpdated = cardRepository.changeCardStatus(id, cardStatus);
        if(rowsUpdated == 0) {
            throw new CardNotFoundException(String.format("Карты с ID = %d не существует", id));
        }
        List<Long> blockingRequestsIds = blockingRequestRepository.findByCardId(id)
                .stream()
                .map(CardBlockingRequest::getId)
                .toList();
        blockingRequestRepository.setCompletedStatusByRequestIds(blockingRequestsIds);
    }

    @Transactional
    public int updateExpiredCards(String expirationDate) {
        LocalDate date = LocalDate.parse(expirationDate);
        return cardRepository.updateExpiredCards(date);
    }

    @Transactional
    public void deleteCardById(long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new CardNotFoundException(String.format("Карты с ID = %d не существует", id)));
        card.getOwner().getCards().remove(card);
        blockingRequestRepository.clearCardsByCardId(id);
        cardRepository.deleteByIdNative(id);
    }

    public List<CardUserDto> getAllUserCards(Long userId, Boolean fullNumber) {
        return cardRepository.findByOwnerId(userId)
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
}
