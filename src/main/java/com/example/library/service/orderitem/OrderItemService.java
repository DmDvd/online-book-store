package com.example.library.service.orderitem;

import com.example.library.dto.orderitem.OrderItemDto;
import java.util.List;

public interface OrderItemService {
    List<OrderItemDto> getOrderItems(Long orderId);

    OrderItemDto getOrderItem(Long orderId, Long itemId);
}
