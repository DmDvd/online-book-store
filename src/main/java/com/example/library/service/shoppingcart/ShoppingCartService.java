package com.example.library.service.shoppingcart;

import com.example.library.dto.cartitem.AddToCartRequestDto;
import com.example.library.dto.cartitem.UpdateCartItemRequestDto;
import com.example.library.dto.shopppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart();

    ShoppingCartDto addBookToCart(AddToCartRequestDto requestDto);

    ShoppingCartDto update(Long cartItemId, UpdateCartItemRequestDto requestDto);

    void removeCartItem(Long cartItemId);
}
