package com.example.bankcards.service;

import com.example.bankcards.dto.user.UserDataChangeRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.user.ExistingPhoneException;
import com.example.bankcards.exception.user.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
@DisplayName("Юнит-тесты класса UserService")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Метод updateUser: успешное обновление данных пользователя")
    void updateUser_shouldUpdateCorrectly() {
        UserDataChangeRequest userDataChangeRequest = new UserDataChangeRequest(
                "new", "new", "new", "new", "new");
        User user = new User();

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(userRepository.findByPhone(userDataChangeRequest.getPhone())).thenReturn(Optional.empty());
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(userDataChangeRequest.getPassword())).thenReturn(encodedPassword);

        userService.updateUser(1L, userDataChangeRequest);

        assertEquals(user.getPassword(), passwordEncoder.encode(userDataChangeRequest.getPassword()));
        assertEquals(user.getPhone(), userDataChangeRequest.getPhone());
        assertEquals(user.getSurname(), userDataChangeRequest.getSurname());
        assertEquals(user.getName(), userDataChangeRequest.getName());
        assertEquals(user.getPatronymic(), userDataChangeRequest.getPatronymic());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Метод updateUser: указанный номер уже зарегистрирован")
    void updateUser_shouldThrowExistingPhoneException() {
        long userId = 1L;
        UserDataChangeRequest userDataChangeRequest = new UserDataChangeRequest(
                "new", "new", "new", "new", "new");
        User user = new User();
        user.setId(userId);
        user.setPhone("+7 777");
        User otherUser = new User();
        otherUser.setId(userId + 1);
        otherUser.setPhone("+7 777");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByPhone(userDataChangeRequest.getPhone())).thenReturn(Optional.of(otherUser));

        assertThrows(ExistingPhoneException.class, () -> userService.updateUser(userId, userDataChangeRequest));
        verify(passwordEncoder, never()).encode(userDataChangeRequest.getPassword());
        verify(userRepository, never()).save(user);
    }

    @Test
    @DisplayName("Метод updateUser: пользователя с переданным ID не существует")
    void updateUser_shouldThrowUserNotFoundException() {
        long userId = 1L;
        UserDataChangeRequest userDataChangeRequest = new UserDataChangeRequest(
                "new", "new", "new", "new", "new");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, userDataChangeRequest));
        verify(userRepository, never()).findByPhone(userDataChangeRequest.getPhone());
        verify(passwordEncoder, never()).encode(userDataChangeRequest.getPassword());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Метод deleteUser: успешное удаление пользователя и всех его карт")
    void deleteUser_shouldThrowUserNotFoundException() {
        User user = new User(); user.setId(1L);
        Card card1 = new Card(); card1.setId(1L); card1.setOwner(user);
        Card card2 = new Card(); card2.setId(2L); card2.setOwner(user);
        List<Card> cards = List.of(card1, card2);
        user.setCards(cards);

        when(userRepository.findByIdWithCards(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(cardRepository).clearOwnerByCardIdIn(any());
        doNothing().when(userRepository).deleteByIdNative(user.getId());

        userService.deleteUser(1L);

        verify(cardRepository).clearOwnerByCardIdIn(any());
        verify(userRepository).deleteByIdNative(user.getId());
    }
}
