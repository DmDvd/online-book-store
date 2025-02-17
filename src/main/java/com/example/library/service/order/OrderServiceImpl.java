package com.example.library.service.order;

import com.example.library.dto.order.CreateOrderRequestDto;
import com.example.library.dto.order.OrderDto;
import com.example.library.dto.order.UpdateOrderStatusRequestDto;
import com.example.library.dto.orderitem.OrderItemDto;
import com.example.library.exception.EntityNotFoundException;
import com.example.library.mapper.OrderItemMapper;
import com.example.library.mapper.OrderMapper;
import com.example.library.model.CartItem;
import com.example.library.model.Order;
import com.example.library.model.OrderItem;
import com.example.library.model.ShoppingCart;
import com.example.library.model.User;
import com.example.library.repository.order.OrderRepository;
import com.example.library.repository.orderitem.OrderItemRepository;
import com.example.library.repository.shoppingcart.ShoppingCartRepository;
import com.example.library.repository.user.UserRepository;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderDto placeOrder(CreateOrderRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by id: " + userId));
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find shopping cart by id: " + userId));
        if (shoppingCart.getCartItem().isEmpty()) {
            throw new IllegalStateException("Shopping cart is empty");
        }
        Order order = createOrder(requestDto, user);

        Set<OrderItem> orderItems = createOrderItems(order, shoppingCart);

        order.setOrderItem(orderItems);
        order.setTotal(calculateTotal(orderItems));

        orderRepository.save(order);

        shoppingCart.getCartItem().clear();
        shoppingCartRepository.save(shoppingCart);

        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId);
        if (orders.isEmpty()) {
            throw new EntityNotFoundException("No orders found for user with id: " + userId);
        }
        return orders.stream().map(orderMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto requestDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id: " + orderId)
        );
        if (!order.getStatus().equals(requestDto.getStatus())) {
            order.setStatus(requestDto.getStatus());
            orderRepository.save(order);
        }
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderItemDto> getOrderItems(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id: "
                        + orderId + " for user id: " + userId)
        );
        return order.getOrderItem()
                .stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getOrderItem(Long orderId, Long itemId, Long userId) {
        OrderItem orderItem = orderItemRepository
                .findByIdAndOrderIdAndOrderUserId(itemId, orderId, userId).orElseThrow(
                    () -> new EntityNotFoundException("Can't find order item by id: " + itemId
                        + " in order id: " + orderId + " for user id: " + userId)
        );
        return orderItemMapper.toDto(orderItem);
    }

    private Order createOrder(CreateOrderRequestDto requestDto, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setShippingAddress(requestDto.getShippingAddress());
        return order;
    }

    private Set<OrderItem> createOrderItems(Order order, ShoppingCart shoppingCart) {
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : shoppingCart.getCartItem()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook()
                    .getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private BigDecimal calculateTotal(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
