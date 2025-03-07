package com.example.library.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.dto.cartitem.AddToCartRequestDto;
import com.example.library.dto.cartitem.CartItemDto;
import com.example.library.dto.cartitem.UpdateCartItemRequestDto;
import com.example.library.dto.shopppingcart.ShoppingCartDto;
import com.example.library.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp() {
        User testUser = new User()
                .setId(1L)
                .setEmail("user@example.com");

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testUser, null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Sql(scripts = "classpath:database/books/shopping-cart/add-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/shopping-cart/delete-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get shopping cart should return valid user cart")
    @Test
    void getShoppingCart_WhenShoppingCartExists_ShouldReturnValidUserCart() throws Exception {
        MvcResult result = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ShoppingCartDto actual = objectMapper.readValue(content, ShoppingCartDto.class);

        System.out.println(result.getResponse().getContentAsString());
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(1L, actual.getUserId());
        assertNotNull(actual.getCartItems());
    }

    @Sql(scripts = "classpath:database/books/shopping-cart/add-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/shopping-cart/delete-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Add book to cart with valid request DTO should succeed")
    @Test
    void addBookToCart_ValidRequestDto_Success() throws Exception {
        AddToCartRequestDto requestDto = new AddToCartRequestDto()
                .setBookId(2L)
                .setQuantity(1);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        System.out.println(result.getResponse().getContentAsString());
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(1L, actual.getUserId());
        assertNotNull(actual.getCartItems());
        CartItemDto cartItem = actual.getCartItems().iterator().next();
        assertEquals(2L, cartItem.getBookId());
        assertEquals(2, cartItem.getQuantity());
    }

    @Sql(scripts = "classpath:database/books/shopping-cart/add-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/shopping-cart/delete-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Add book to cart when request DTO is invalid should return bad request")
    @Test
    void addBookToCart_WhenRequestDtoIsInvalid_ShouldReturnBadRequest() throws Exception {
        AddToCartRequestDto requestDto = new AddToCartRequestDto()
                .setQuantity(1);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Sql(scripts = "classpath:database/books/shopping-cart/add-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/shopping-cart/delete-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update cart item with valid update shopping cart request should succeed")
    @Test
    void updateCartItem_ValidUpdateShoppingCart_Success() throws Exception {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto()
                .setQuantity(3);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put("/cart/items/{cartItemId}", 1L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(1L, actual.getUserId());
        assertNotNull(actual.getCartItems());
        CartItemDto cartItem = actual.getCartItems().iterator().next();
        assertEquals(2L, cartItem.getBookId());
        assertEquals(3, cartItem.getQuantity());
    }

    @Sql(scripts = "classpath:database/books/shopping-cart/add-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/shopping-cart/delete-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update cart item when cart item not found should return not found")
    @Test
    void updateCartItem_WhenCartItemNotFound_ShouldReturnNotFound() throws Exception {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto()
                .setQuantity(3);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/cart/items/{cartItemId}",
                        999L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Sql(scripts = "classpath:database/books/shopping-cart/add-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/shopping-cart/delete-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Delete shopping cart by cart item ID should return no content")
    @Test
    void removeCartItem_DeleteShoppingCartByCartItemId_ShouldReturnNoContent()
            throws Exception {
        mockMvc.perform(delete("/cart/items/{cartItemId}", 1L))
                .andExpect(status().isNoContent());
    }

    @Sql(scripts = "classpath:database/books/shopping-cart/add-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/shopping-cart/delete-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Remove cart item when not authorized should return unauthorized")
    @Test
    void removeCartItem_WhenNotAuthorized_ShouldReturnUnauthorized() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(delete("/cart/items/{cartItemId}", 1L))
                .andExpect(status().isUnauthorized());
    }
}
