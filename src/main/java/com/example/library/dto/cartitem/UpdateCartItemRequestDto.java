package com.example.library.dto.cartitem;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateCartItemRequestDto {
    @Positive
    private int quantity;
}
