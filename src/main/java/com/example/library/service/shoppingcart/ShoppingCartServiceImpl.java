package com.example.library.service.shoppingcart;

import com.example.library.dto.cartitem.AddToCartRequestDto;
import com.example.library.dto.cartitem.UpdateCartItemRequestDto;
import com.example.library.dto.shopppingcart.ShoppingCartDto;
import com.example.library.exception.EntityNotFoundException;
import com.example.library.mapper.CartItemMapper;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper itemMapper;

    @Override
    public ShoppingCartDto getShoppingCart(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUser(user);

                    return shoppingCartRepository.save(newCart);
                });

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addBookToCart(Long userId, AddToCartRequestDto requestDto) {

        Book book = bookRepository.findById(requestDto.getBookId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: "
                        + requestDto.getBookId()));

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find shopping cart by id: " + userId)
        );
        shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(requestDto.getBookId()))
                        .findFirst()
                                .ifPresentOrElse(item -> item.setQuantity(item.getQuantity() + requestDto.getQuantity()),
                                        () -> addCartItemToCart(requestDto, book, shoppingCart));
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto update(Long userId,
                                  Long cartItemId,
                                  UpdateCartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find shopping cart for user with id: "
                                + userId)
        );
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId,
                        shoppingCart.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item not found in user's shopping cart, id: " + cartItemId));
        cartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Can't find shopping cart for user with id: "
                                + userId)
        );
        CartItem cartItem = cartItemRepository
                .findByIdAndShoppingCartId(cartItemId,
                        shoppingCart.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Cart item not found in user's shopping cart, "
                                + "id: " + cartItemId)
        );
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    private void addCartItemToCart(AddToCartRequestDto itemDto, Book book, ShoppingCart cart) {
        CartItem cartItem = itemMapper.toModel(itemDto);
        cartItem.setBook(book);
        cart.addItemToCart(cartItem);
    }
}
