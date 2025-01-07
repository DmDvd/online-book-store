package com.example.library.repository;

import com.example.library.model.Book;
import java.util.List;

public interface BookRepository {
    Book createBook(Book book);

    Book getBookById(Long id);

    List<Book> getAll();
}
