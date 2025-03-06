package com.example.library.mapper;

import com.example.library.config.MapperConfig;
import com.example.library.dto.shopppingcart.ShoppingCartDto;
import com.example.library.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "user.id")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
