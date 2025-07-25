package com.example.bankcards.service;

import com.example.bankcards.dto.UserDataChangeRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ExistingPhoneException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updateUser(long id, UserDataChangeRequest userDataChangeRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователя с ID = %d не существует", id)));
        User userWithPhone = userRepository.findByPhone(userDataChangeRequest.getPhone()).orElse(null);
        if(userWithPhone != null) {
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
}
