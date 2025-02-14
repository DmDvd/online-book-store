package com.example.library.service.orderitem;

import com.example.library.dto.orderitem.OrderItemDto;
import com.example.library.exception.EntityNotFoundException;
import com.example.library.mapper.OrderItemMapper;
import com.example.library.model.Order;
import com.example.library.model.OrderItem;
import com.example.library.repository.order.OrderRepository;
import com.example.library.repository.orderitem.OrderItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItemDto> getOrderItems(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id: " + orderId)
        );
        return order.getOrderItem()
                .stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getOrderItem(Long orderId, Long itemId) {
        OrderItem orderItem = orderItemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order item by id: " + itemId)
                );
        return orderItemMapper.toDto(orderItem);
    }
}
