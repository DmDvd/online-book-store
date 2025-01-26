package com.example.library.service;

import com.example.library.dto.BookDto;
import com.example.library.dto.BookSearchParametersDto;
import com.example.library.dto.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto createBook(CreateBookRequestDto requestDto);

    BookDto getBookById(Long id);

    List<BookDto> getAll(Pageable pageable);

    void deleteById(Long id);

    BookDto updateBook(Long id, CreateBookRequestDto requestDto);

    List<BookDto> search(BookSearchParametersDto params);
}
