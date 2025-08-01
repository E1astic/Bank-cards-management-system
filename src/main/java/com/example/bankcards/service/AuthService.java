package com.example.bankcards.service;

import com.example.bankcards.dto.user.UserLoginRequest;
import com.example.bankcards.dto.user.UserRegisterRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.user.ExistingEmailException;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.converter.UserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final JwtService jwtService;
    private final UserConverter userConverter;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;


    public void register(UserRegisterRequest userRegisterRequest) {
        try {
            userDetailsService.loadUserByUsername(userRegisterRequest.getEmail());
            throw new ExistingEmailException();
        } catch (UsernameNotFoundException e) {
            User user = userConverter.mapToUser(userRegisterRequest);
            user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
            userService.saveUser(user);
        }
    }

    public String login(UserLoginRequest userLoginRequest) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(
                userLoginRequest.getEmail(), userLoginRequest.getPassword()));
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return jwtService.generateToken(customUserDetails);
    }
}
