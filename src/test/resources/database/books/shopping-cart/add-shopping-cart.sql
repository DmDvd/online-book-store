INSERT INTO shopping_carts (user_id, is_deleted)
VALUES (1, false);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (2, 'Book Title', 'Author Name', '123456789', 19.99, 'Description of the book', 'coverImage.jpg', false);

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity, is_deleted)
VALUES (1, 1, 2, 1, false);