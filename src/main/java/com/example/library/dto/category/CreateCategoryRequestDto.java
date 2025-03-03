package com.example.library.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class CreateCategoryRequestDto {
    @NotBlank
    private String name;
    private String description;
}
