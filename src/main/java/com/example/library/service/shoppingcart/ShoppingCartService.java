package com.example.library.service.shoppingcart;

import com.example.library.dto.cartitem.AddToCartRequestDto;
import com.example.library.dto.cartitem.UpdateCartItemRequestDto;
import com.example.library.dto.shopppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(Long userId);

    ShoppingCartDto addBookToCart(Long userId, AddToCartRequestDto requestDto);

    ShoppingCartDto update(Long userId, Long cartItemId, UpdateCartItemRequestDto requestDto);

    void removeCartItem(Long userId, Long cartItemId);
}
