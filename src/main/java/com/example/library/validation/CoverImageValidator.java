package com.example.library.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CoverImageValidator implements ConstraintValidator<CoverImage, String> {
    private static final String PATTERN_OF_URL = "^(https?):\\/\\/[^\\s$.?#].[^\\s]"
            + "*\\.(jpg|jpeg|png|gif)$";

    @Override
    public boolean isValid(String coverImage,
                           ConstraintValidatorContext constraintValidatorContext) {
        return coverImage != null && Pattern.compile(PATTERN_OF_URL).matcher(coverImage).matches();
    }
}
