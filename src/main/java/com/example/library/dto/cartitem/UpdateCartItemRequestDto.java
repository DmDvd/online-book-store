package com.example.library.dto.cartitem;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class UpdateCartItemRequestDto {
    @Positive
    private int quantity;
}
