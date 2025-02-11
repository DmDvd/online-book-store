package com.example.library.mapper;

import com.example.library.config.MapperConfig;
import com.example.library.dto.cartitem.AddToCartRequestDto;
import com.example.library.dto.cartitem.CartItemDto;
import com.example.library.model.CartItem;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    CartItemDto toDto(CartItem cartItem);

    CartItem toModel(AddToCartRequestDto requestDto);
}
