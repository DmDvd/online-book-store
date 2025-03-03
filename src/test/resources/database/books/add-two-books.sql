INSERT INTO books (id, title, author, isbn, price, description, cover_image) VALUES
(1, 'Sample Book 1', 'Author A', '0-306-40615-2', 149.99, 'Another sample book description A', 'http://example.com/cover1.jpg'),
(2, 'Sample Book 2', 'Author B', '0-405-50617-3', 249.99, 'Another sample book description B', 'http://example.com/cover2.jpg');

INSERT INTO categories (id, name) VALUES (1, 'Fiction'), (2, 'Science');

INSERT INTO books_categories (book_id, category_id) VALUES (1, 1), (1, 2), (2, 1);