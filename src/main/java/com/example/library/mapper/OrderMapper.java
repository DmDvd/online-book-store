package com.example.library.mapper;

import com.example.library.dto.order.CreateOrderRequestDto;
import com.example.library.dto.order.OrderDto;
import com.example.library.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order order);

    Order toModel(CreateOrderRequestDto requestDto);
}
