package com.example.bankcards.service;

import com.example.bankcards.converter.UserConverter;
import com.example.bankcards.dto.user.UserLoginRequest;
import com.example.bankcards.dto.user.UserRegisterRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.user.ExistingEmailException;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Юнит-тесты класса AuthService")
public class AuthServiceTest {

    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserConverter userConverter;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;
    
    @Test
    @DisplayName("Метод register: успешное сохранение пользователя")
    void register_shouldSaveUser() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        User user = new User();

        when(userDetailsService.loadUserByUsername(userRegisterRequest.getEmail()))
                .thenThrow(new UsernameNotFoundException(""));
        when(userConverter.mapToUser(userRegisterRequest)).thenReturn(user);
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(userRegisterRequest.getPassword())).thenReturn(encodedPassword);

        authService.register(userRegisterRequest);

        verify(userDetailsService).loadUserByUsername(userRegisterRequest.getEmail());
        verify(userConverter).mapToUser(userRegisterRequest);
        assertEquals(encodedPassword, user.getPassword());
        verify(userService).saveUser(user);
    }

    @Test
    @DisplayName("Метод register: данный email уже зарегистрирован")
    void register_shouldThrowExistingEmailException() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        CustomUserDetails userDetails = new CustomUserDetails(
                "", "", (long) 0, Collections.emptyList());

        when(userDetailsService.loadUserByUsername(userRegisterRequest.getEmail()))
                .thenReturn(userDetails);

        assertThrows(ExistingEmailException.class, () -> authService.register(userRegisterRequest));
        verify(userConverter, never()).mapToUser(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userService, never()).saveUser(any());
    }

    @Test
    @DisplayName("Метод login: успешная генерация JWT")
    void login_shouldGenerateToken() {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        CustomUserDetails userDetails = new CustomUserDetails(
                "", "", (long) 0, Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        String expectedToken = "token";
        when(jwtService.generateToken(userDetails)).thenReturn(expectedToken);

        String actualToken = authService.login(userLoginRequest);

        assertEquals(expectedToken, actualToken);
    }

    @Test
    @DisplayName("Метод login: неверный логин или пароль")
    void login_shouldThrowBadCredentialsException() {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        assertThrows(BadCredentialsException.class, () -> authService.login(userLoginRequest));
        verify(jwtService, never()).generateToken(any());
    }
}
