package com.example.library.config;

import com.example.library.dto.book.BookDto;
import com.example.library.dto.book.CreateBookRequestDto;
import com.example.library.dto.category.CategoryDto;
import com.example.library.dto.category.CreateCategoryRequestDto;
import java.math.BigDecimal;
import java.util.List;

public class TestUtil {
    public static CreateBookRequestDto createBookRequestDto() {
        return new CreateBookRequestDto()
                .setTitle("Sample Book 1")
                .setAuthor("Author B")
                .setIsbn("0-306-40615-2")
                .setPrice(BigDecimal.valueOf(149.99))
                .setDescription("Another sample book description.")
                .setCoverImage("http://example.com/cover1.jpg")
                .setCategoriesId(List.of(1L));
    }

    public static BookDto createBookDto(Long id) {
        CreateBookRequestDto requestDto = createBookRequestDto();
        return new BookDto()
                .setId(id)
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage())
                .setCategoryIds(requestDto.getCategoriesId());
    }

    public static CreateCategoryRequestDto createCategoryRequestDto() {
        return new CreateCategoryRequestDto()
                .setName("Fiction")
                .setDescription("Fiction books");
    }

    public static CategoryDto createCategoryDto(Long id) {
        CreateCategoryRequestDto requestDto = createCategoryRequestDto();
        return new CategoryDto()
                .setId(id)
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());
    }
}
