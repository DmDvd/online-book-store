package com.example.library.service;

import com.example.library.dto.BookDto;
import com.example.library.dto.CreateBookRequestDto;
import com.example.library.mapper.BookMapper;
import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.createBook(book));
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.getBookById(id);
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> getAll() {
        return bookRepository.getAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
