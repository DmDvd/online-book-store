package com.example.library.config;

import com.example.library.dto.book.BookDto;
import java.util.List;
import lombok.Data;

@Data
public class PagedBookResponse {
    private List<BookDto> content;
    private int totalElements;
    private int totalPages;
}
