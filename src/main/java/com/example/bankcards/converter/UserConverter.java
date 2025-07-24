package com.example.bankcards.converter;

import com.example.bankcards.dto.UserRegisterRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    private Role userRole;

    @PostConstruct
    public void getUserRole() {
        userRole = roleRepository.findByName("ROLE_USER");
    }

    public User mapToUser(UserRegisterRequest userRegisterRequest) {
        User user = modelMapper.map(userRegisterRequest, User.class);
        user.setRole(userRole);
        return user;
    }
}
