package com.example.library.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class AddToCartRequestDto {
    @NotNull
    @Positive
    private Long bookId;
    @Positive
    private int quantity;
}
