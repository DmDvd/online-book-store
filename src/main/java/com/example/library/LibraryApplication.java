package com.example.library;

import com.example.library.service.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class LibraryApplication {

    private final BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}
