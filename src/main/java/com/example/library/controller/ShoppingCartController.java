package com.example.library.controller;

import com.example.library.dto.cartitem.AddToCartRequestDto;
import com.example.library.dto.cartitem.UpdateCartItemRequestDto;
import com.example.library.dto.shopppingcart.ShoppingCartDto;
import com.example.library.model.User;
import com.example.library.service.shoppingcart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping carts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Get user cart", description = "Retrieve user's shopping cart")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        Long userId = getUserId(authentication);
        return shoppingCartService.getShoppingCart(userId);
    }

    @Operation(summary = "Add book to the shopping cart",
            description = "Add book to the shopping cart")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ShoppingCartDto addBookToCart(Authentication authentication,
                                         @RequestBody @Valid AddToCartRequestDto requestDto) {
        Long userId = getUserId(authentication);
        return shoppingCartService.addBookToCart(userId, requestDto);
    }

    @Operation(summary = "Update quantity",
            description = "Update quantity of a book in the shopping cart")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/items/{cartItemId}")
    public ShoppingCartDto updateCartItem(Authentication authentication,
                                          @PathVariable Long cartItemId,
                                          @RequestBody @Valid UpdateCartItemRequestDto requestDto) {
        Long userId = getUserId(authentication);
        return shoppingCartService.update(userId, cartItemId, requestDto);
    }

    @Operation(summary = "Remove a book from the shopping cart",
            description = "Remove a book from the shopping cart")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/items/{cartItemId}")
    public void removeCartItem(Authentication authentication, @PathVariable Long cartItemId) {
        Long userId = getUserId(authentication);
        shoppingCartService.removeCartItem(userId, cartItemId);
    }

    private Long getUserId(Authentication authentication) {
        return ((User) authentication.getPrincipal()).getId();
    }
}
