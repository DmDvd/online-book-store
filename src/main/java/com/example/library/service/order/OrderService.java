package com.example.library.service.order;

import com.example.library.dto.order.CreateOrderRequestDto;
import com.example.library.dto.order.OrderDto;
import com.example.library.dto.order.UpdateOrderStatusRequestDto;
import com.example.library.dto.orderitem.OrderItemDto;
import java.util.List;

public interface OrderService {
    OrderDto placeOrder(CreateOrderRequestDto requestDto, Long userId);

    List<OrderDto> getUserOrders(Long userId);

    OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto requestDto);

    List<OrderItemDto> getOrderItems(Long orderId);

    OrderItemDto getOrderItem(Long orderId, Long itemId);
}
