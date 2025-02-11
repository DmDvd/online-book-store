package com.example.library.controller;

import com.example.library.dto.cartitem.AddToCartRequestDto;
import com.example.library.dto.cartitem.UpdateCartItemRequestDto;
import com.example.library.dto.shopppingcart.ShoppingCartDto;
import com.example.library.service.shoppingcart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "endpoints for managing shopping carts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Get user cart", description = "Retrieve user's shopping cart")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public ShoppingCartDto getShoppingCart() {
        return shoppingCartService.getShoppingCart();
    }

    @Operation(summary = "Add book to the shopping cart",
            description = "Add book to the shopping cart")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ShoppingCartDto addBookToCart(@RequestBody @Valid AddToCartRequestDto requestDto) {
        return shoppingCartService.addBookToCart(requestDto);
    }

    @Operation(summary = "Update quantity of a book in the shopping cart",
            description = "Update quantity of a book in the shopping cart")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/items/{cartItemId}")
    public ShoppingCartDto updateCartItem(@PathVariable Long cartItemId,
                                          @RequestBody @Valid UpdateCartItemRequestDto requestDto) {
        return shoppingCartService.update(cartItemId, requestDto);
    }

    @Operation(summary = "Remove a book from the shopping cart",
            description = "Remove a book from the shopping cart")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/items/{cartItemId}")
    public void removeCartItem(@PathVariable Long cartItemId) {
        shoppingCartService.removeCartItem(cartItemId);
    }
}
