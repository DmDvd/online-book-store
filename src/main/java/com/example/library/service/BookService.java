package com.example.library.service;

import com.example.library.dto.BookDto;
import com.example.library.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto createBook(CreateBookRequestDto requestDto);

    BookDto getBookById(Long id);

    List<BookDto> getAll();

    void deleteById(Long id);

    BookDto updateBook(Long id, CreateBookRequestDto requestDto);
}
