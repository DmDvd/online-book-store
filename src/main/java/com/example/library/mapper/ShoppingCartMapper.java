package com.example.library.mapper;

import com.example.library.config.MapperConfig;
import com.example.library.dto.shopppingcart.ShoppingCartDto;
import com.example.library.model.ShoppingCart;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
