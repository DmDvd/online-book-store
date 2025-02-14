package com.example.library.controller;

import com.example.library.dto.order.CreateOrderRequestDto;
import com.example.library.dto.order.OrderDto;
import com.example.library.dto.order.UpdateOrderStatusRequestDto;
import com.example.library.dto.orderitem.OrderItemDto;
import com.example.library.model.User;
import com.example.library.service.order.OrderService;
import com.example.library.service.orderitem.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @Operation(summary = "Create an order",
            description = "Place an order for books in the shopping cart")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public OrderDto createOrder(Authentication authentication,
                                @RequestBody @Valid CreateOrderRequestDto requestDto) {
        Long userId = getUserId(authentication);
        return orderService.placeOrder(requestDto, userId);
    }

    @Operation(summary = "Get order history", description = "Retrieve all past orders of the user")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public List<OrderDto> getOrderHistory(Authentication authentication) {
        Long userId = getUserId(authentication);
        return orderService.getUserOrders(userId);
    }

    @Operation(summary = "Get order items",
            description = "Retrieve all items from a specific order")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{orderId}/items")
    public List<OrderItemDto> gettingItemsOnTheOrders(@PathVariable Long orderId) {
        return orderItemService.getOrderItems(orderId);
    }

    @Operation(summary = "Get order item", description = "Retrieve item from a specific order")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItems(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderItemService.getOrderItem(orderId, itemId);
    }

    @Operation(summary = "Update order status", description = "Change the status of an order")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public OrderDto updateOrderStatus(@PathVariable Long id,
                                      @RequestBody @Valid UpdateOrderStatusRequestDto requestDto) {
        return orderService.updateOrderStatus(id, requestDto);
    }

    private Long getUserId(Authentication authentication) {
        return ((User) authentication.getPrincipal()).getId();
    }
}
