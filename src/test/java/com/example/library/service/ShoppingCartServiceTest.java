package com.example.library.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import com.example.library.service.shoppingcart.ShoppingCartServiceImpl;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartItemMapper itemMapper;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Get shopping cart for invalid user Id throws EntityNotFoundException")
    void getShoppingCart_InvalidUserId_ThrowsEntityNotFoundException() {
        Long userId = 100L;
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> shoppingCartService.getShoppingCart(userId)
        );
        String expected = "Can't find shopping cart by id: " + userId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Add book to cart for valid book and cart adds or updates cart item")
    void addBookToCart_ValidBookAndCart_AddsOrUpdatesCartItem() {
        Long userId = 1L;
        final Long bookId = 1L;
        final int quantityToAdd = 1;

        User user = new User()
                .setId(userId)
                .setEmail("test@example.com");

        final ShoppingCart shoppingCart = new ShoppingCart()
                .setId(userId)
                .setUser(user)
                .setCartItems(new HashSet<>());

        Book book = new Book()
                .setId(bookId)
                .setTitle("Sample Book 1")
                .setAuthor("Author B")
                .setIsbn("0-306-40615-2")
                .setPrice(BigDecimal.valueOf(149.99))
                .setDescription("Another sample book description.")
                .setCoverImage("http://example.com/cover1.jpg");

        ShoppingCartDto expectedDto = new ShoppingCartDto();
        expectedDto.setId(userId);

        AddToCartRequestDto requestDto = new AddToCartRequestDto()
                .setBookId(bookId)
                .setQuantity(quantityToAdd);
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(quantityToAdd);

        when(itemMapper.toModel(requestDto)).thenReturn(cartItem);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.of(book));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedDto);

        ShoppingCartDto actualDto = shoppingCartService.addBookToCart(userId, requestDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(shoppingCartRepository).findByUserId(userId);
        verify(bookRepository).findById(bookId);
        verify(shoppingCartMapper).toDto(shoppingCart);
    }

    @Test
    @DisplayName("Update cart item for valid cart and item updates quantity")
    void updateCartItem_ValidCartAndItem_UpdatesQuantity() {
        Long userId = 1L;
        final Long cartItemId = 1L;
        final Long bookId = 1L;
        final int initialQuantity = 2;

        User user = new User()
                .setId(userId)
                .setEmail("test@example.com");

        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(userId)
                .setUser(user)
                .setCartItems(new HashSet<>());

        Book book = new Book()
                .setId(bookId)
                .setTitle("Sample Book 1")
                .setAuthor("Author B")
                .setIsbn("0-306-40615-2")
                .setPrice(BigDecimal.valueOf(149.99))
                .setDescription("Another sample book description.")
                .setCoverImage("http://example.com/cover1.jpg");

        CartItem cartItem = new CartItem()
                .setId(cartItemId)
                .setShoppingCart(shoppingCart)
                .setBook(book)
                .setQuantity(initialQuantity);

        shoppingCart.getCartItems().add(cartItem);

        final UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto()
                .setQuantity(1);

        ShoppingCartDto expectedDto = new ShoppingCartDto();
        expectedDto.setId(userId);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedDto);

        ShoppingCartDto actual = shoppingCartService.update(userId, cartItemId, requestDto);

        assertNotNull(actual);
        assertEquals(expectedDto, actual);
        assertEquals(1, cartItem.getQuantity());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when shopping cart is not found")
    void updateCartItem_WhenShoppingCartNotFound_ShouldThrowException() {
        Long userId = 1L;
        Long cartItemId = 1L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto().setQuantity(2);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.update(userId, cartItemId, requestDto)
        );

        assertEquals("Can't find shopping cart for user with id: "
                + userId, exception.getMessage());

        verify(shoppingCartRepository).findByUserId(userId);
        verifyNoInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("Get shopping cart for valid user Id returns ShoppingCartDto")
    void getShoppingCart_ValidUserId_ReturnsShoppingCartDto() {
        Long userId = 1L;
        User user = new User()
                .setId(userId)
                .setEmail("test@example.com");

        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(userId)
                .setUser(user)
                .setCartItems(new HashSet<>());

        ShoppingCartDto expectedDto = new ShoppingCartDto();
        expectedDto.setId(userId);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedDto);

        ShoppingCartDto actualDto = shoppingCartService.getShoppingCart(userId);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(shoppingCartRepository).findByUserId(userId);
        verify(shoppingCartMapper).toDto(shoppingCart);
    }

    @Test
    @DisplayName("Remove cart item when item not found throws exception")
    void removeCartItem_ItemNotFound_ThrowsException() {
        Long userId = 1L;
        Long cartItemId = 99L;

        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(userId);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, shoppingCart.getId()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> shoppingCartService.removeCartItem(userId, cartItemId));

        verify(cartItemRepository, never()).delete(any());
    }
}
