package com.example.library.mapper;

import com.example.library.config.MapperConfig;
import com.example.library.dto.orderitem.OrderItemDto;
import com.example.library.model.Order;
import com.example.library.model.OrderItem;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    OrderItemDto toDto(OrderItem orderItem);

    Order toModel(OrderItemDto orderItemDto);
}
