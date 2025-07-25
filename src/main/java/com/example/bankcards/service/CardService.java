package com.example.bankcards.service;

import com.example.bankcards.converter.CardConverter;
import com.example.bankcards.dto.CardAdminDto;
import com.example.bankcards.dto.CardRegisterRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.EncryptionException;
import com.example.bankcards.exception.IncorrectCardStatusException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CryptoUtil;
import com.example.bankcards.util.enums.CardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<CardAdminDto> getAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(cardConverter::mapToCardAdminDto)
                .toList();
    }

    public CardAdminDto getCardById(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new CardNotFoundException(String.format("Карты с ID = %d не существует", id)));
        return cardConverter.mapToCardAdminDto(card);
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
        CardStatus cardStatus = CardStatus.valueOf(newStatus.toUpperCase());
        int rowsUpdated = cardRepository.changeCardStatus(id, cardStatus);
        if(rowsUpdated == 0) {
            throw new CardNotFoundException(String.format("Карты с ID = %d не существует", id));
        }
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
        cardRepository.deleteById(id);
    }
}
