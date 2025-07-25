package com.example.bankcards.converter;

import com.example.bankcards.dto.CardAdminDto;
import com.example.bankcards.dto.CardRegisterRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.CryptoUtil;
import com.example.bankcards.util.enums.CardStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CardConverter {

    private final ModelMapper modelMapper;
    private final CryptoUtil cryptoUtil;

    public Card mapToCard(CardRegisterRequest cardRegisterRequest, LocalDate activationDate, User owner) {
        return Card.builder()
                .number(cardRegisterRequest.getNumber())
                .activationDate(activationDate)
                .expirationDate(activationDate.plusYears(cardRegisterRequest.getExpirationYears()))
                .status(CardStatus.ACTIVE)
                .owner(owner)
                .balance(new BigDecimal(0))
                .build();
    }

    public CardAdminDto mapToCardAdminDto(Card card) {
        CardAdminDto cardAdminDto = modelMapper.map(card, CardAdminDto.class);
        cardAdminDto.setOwnerId(card.getOwner().getId());
        cardAdminDto.setNumber(cryptoUtil.decrypt(card.getNumber()));
        return cardAdminDto;
    }
}
