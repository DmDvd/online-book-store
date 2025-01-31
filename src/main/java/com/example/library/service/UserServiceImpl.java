package com.example.library.service;

import com.example.library.dto.user.UserRegistrationRequestDto;
import com.example.library.dto.user.UserResponseDto;
import com.example.library.exception.RegistrationException;
import com.example.library.mapper.UserMapper;
import com.example.library.model.User;
import com.example.library.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) throws
            RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Can't register user");
        }
        User user = userMapper.toModel(requestDto);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
}
