package com.example.bankcards.service;

import com.example.bankcards.converter.CardConverter;
import com.example.bankcards.dto.card.CardAdminDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardAdminService {

    private final UserRepository userRepository;
    private final BlockingRequestRepository blockingRequestRepository;
    private final CardRepository cardRepository;
    private final CardConverter cardConverter;
    private final CardNumberCryptoUtil cryptoUtil;
    private final CardNumberGenerator cardNumberGenerator;

    public List<CardAdminDto> getAllCards(Boolean fullNumber, Integer size, Integer page) {
        PageRequest pageRequest = validateAndGetPageRequest(page, size);
        return cardRepository.findAll(pageRequest)
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
        String cardNumber = cardNumberGenerator.generateNumber();
        Card card = cardConverter.mapToCard(cardRegisterRequest, cardNumber, LocalDate.now(), owner);
        card.setNumber(cryptoUtil.encrypt(cardNumber));
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
        if(cardStatus == CardStatus.BLOCKED) {
            List<Long> blockingRequestsIds = blockingRequestRepository.findByCardId(id)
                    .stream()
                    .map(CardBlockingRequest::getId)
                    .toList();
            blockingRequestRepository.setCompletedStatusByRequestIds(blockingRequestsIds);
        }
    }

    @Transactional
    public int updateExpiredCards(LocalDate expirationDate) {
        return cardRepository.updateExpiredCards(expirationDate);
    }

    @Transactional
    public void deleteCardById(long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new CardNotFoundException(String.format("Карты с ID = %d не существует", id)));
        card.getOwner().getCards().remove(card);
        blockingRequestRepository.clearCardsByCardId(id);
        cardRepository.deleteByIdNative(id);
    }

    private PageRequest validateAndGetPageRequest(int page, int size) {
        size = (size <= 0) ? 1 : size;
        page = (page < 0) ? 0 : page;
        return PageRequest.of(page, size);
    }
}
