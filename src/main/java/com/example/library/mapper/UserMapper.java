package com.example.library.mapper;

import com.example.library.config.MapperConfig;
import com.example.library.dto.user.UserRegistrationRequestDto;
import com.example.library.dto.user.UserResponseDto;
import com.example.library.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponse(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    void updateUserFromDto(UserRegistrationRequestDto requestDto, @MappingTarget User user);
}
