package com.example.bankcards.service;

import com.example.bankcards.dto.user.UserDataChangeRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.user.ExistingPhoneException;
import com.example.bankcards.exception.user.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CardRepository cardRepository;

    @Transactional
    public void updateUser(long id, UserDataChangeRequest userDataChangeRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователя с ID = %d не существует", id)));
        User userWithPhone = userRepository.findByPhone(userDataChangeRequest.getPhone()).orElse(null);
        if(userWithPhone != null && !Objects.equals(userWithPhone.getId(), user.getId())) {
            throw new ExistingPhoneException(String.format(
                    "Номер %s уже зарегистрирован", userDataChangeRequest.getPhone()));
        }
        user.setPhone(userDataChangeRequest.getPhone());
        user.setPassword(passwordEncoder.encode(userDataChangeRequest.getPassword()));
        user.setSurname(userDataChangeRequest.getSurname());
        user.setName(userDataChangeRequest.getName());
        user.setPatronymic(userDataChangeRequest.getPatronymic());
        userRepository.save(user);
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findByIdWithCards(id).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователя с ID = %d не существует", id)));
        List<Long> cardIds = user.getCards()
                .stream()
                .map(Card::getId)
                .toList();
        cardRepository.clearOwnerByCardIdIn(cardIds);
        userRepository.deleteByIdNative(id);
    }
}
