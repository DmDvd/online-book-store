package com.example.library.service;

import com.example.library.dto.book.BookDto;
import com.example.library.dto.book.BookSearchParametersDto;
import com.example.library.dto.book.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto createBook(CreateBookRequestDto requestDto);

    BookDto getBookById(Long id);

    Page<BookDto> getAll(Pageable pageable);

    void deleteById(Long id);

    BookDto updateBook(Long id, CreateBookRequestDto requestDto);

    List<BookDto> search(BookSearchParametersDto params);
}
