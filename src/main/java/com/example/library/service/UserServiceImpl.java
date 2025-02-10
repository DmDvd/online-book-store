package com.example.library.service;

import com.example.library.dto.user.UserRegistrationRequestDto;
import com.example.library.dto.user.UserResponseDto;
import com.example.library.exception.RegistrationException;
import com.example.library.mapper.UserMapper;
import com.example.library.model.Role;
import com.example.library.model.User;
import com.example.library.repository.role.RoleRepository;
import com.example.library.repository.user.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) throws
            RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Can't register user, email already exists");
        }
        User user = userMapper.toModel(requestDto);
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(userRole));
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
}
