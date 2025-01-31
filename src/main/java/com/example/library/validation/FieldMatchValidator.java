package com.example.library.validation;

import com.example.library.dto.user.UserRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch,
        UserRegistrationRequestDto> {
    @Override
    public boolean isValid(UserRegistrationRequestDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        return dto.getPassword() != null
                && Objects.equals(dto.getPassword(), dto.getRepeatPassword());
    }
}
