package com.example.library.repository.cartitem;

import com.example.library.model.Book;
import com.example.library.model.CartItem;
import com.example.library.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByShoppingCartAndBook(ShoppingCart shoppingCart, Book book);
}
