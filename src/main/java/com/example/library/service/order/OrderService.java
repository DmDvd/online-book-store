package com.example.library.service.order;

import com.example.library.dto.order.CreateOrderRequestDto;
import com.example.library.dto.order.OrderDto;
import com.example.library.dto.order.UpdateOrderStatusRequestDto;
import java.util.List;

public interface OrderService {
    OrderDto placeOrder(CreateOrderRequestDto requestDto, Long userId);

    List<OrderDto> getUserOrders(Long userId);

    OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto requestDto);
}
