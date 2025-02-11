package com.example.library.service.shoppingcart;

import com.example.library.dto.cartitem.AddToCartRequestDto;
import com.example.library.dto.cartitem.UpdateCartItemRequestDto;
import com.example.library.dto.shopppingcart.ShoppingCartDto;
import com.example.library.exception.EntityNotFoundException;
import com.example.library.mapper.ShoppingCartMapper;
import com.example.library.model.Book;
import com.example.library.model.CartItem;
import com.example.library.model.ShoppingCart;
import com.example.library.model.User;
import com.example.library.repository.book.BookRepository;
import com.example.library.repository.cartitem.CartItemRepository;
import com.example.library.repository.shoppingcart.ShoppingCartRepository;
import com.example.library.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public ShoppingCartDto getShoppingCart() {
        User user = getAuthenticatedUser();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find shopping cart by id: " + user.getId())
        );
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addBookToCart(AddToCartRequestDto requestDto) {
        User user = getAuthenticatedUser();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId()).orElseGet(
                () -> shoppingCartRepository.save(new ShoppingCart(user)));
        Book book = bookRepository.findById(requestDto.getBookId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: "
                        + requestDto.getBookId()));
        CartItem cartItem = cartItemRepository.findByShoppingCartAndBook(shoppingCart, book)
                .orElseGet(() -> {
                    CartItem newCartItem = new CartItem(shoppingCart,
                            book, requestDto.getQuantity());
                    shoppingCart.getCartItem().add(newCartItem);
                    return newCartItem;
                });
        cartItem.setQuantity(cartItem.getQuantity() + requestDto.getQuantity());
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto update(Long cartItemId, UpdateCartItemRequestDto requestDto) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Cart item not found with id: " + cartItemId)
        );
        cartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(cartItem);
        ShoppingCart shoppingCart = cartItem.getShoppingCart();
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void removeCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Cart item not found with id: " + cartItemId)
        );
        cartItemRepository.delete(cartItem);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found with email: " + email));
    }
}
