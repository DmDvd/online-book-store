package com.example.library.validation;

import com.example.library.dto.user.UserRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch,
        UserRegistrationRequestDto> {
    @Override
    public boolean isValid(UserRegistrationRequestDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        return dto.getPassword() != null && dto.getPassword().equals(dto.getRepeatPassword());
    }
}
