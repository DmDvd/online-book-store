package com.example.library.mapper;

import com.example.library.config.MapperConfig;
import com.example.library.dto.orderitem.OrderItemDto;
import com.example.library.model.Order;
import com.example.library.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toDto(OrderItem orderItem);

    Order toModel(OrderItemDto orderItemDto);
}
