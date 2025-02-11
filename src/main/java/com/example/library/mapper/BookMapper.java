package com.example.library.mapper;

import com.example.library.config.MapperConfig;
import com.example.library.dto.book.BookDto;
import com.example.library.dto.book.BookDtoWithoutCategoryIds;
import com.example.library.dto.book.CreateBookRequestDto;
import com.example.library.model.Book;
import com.example.library.model.Category;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    void updateBookFromDto(CreateBookRequestDto requestDto, @MappingTarget Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        List<Long> category = book.getCategories()
                .stream()
                .map(Category::getId)
                .toList();
        bookDto.setCategoryIds(category);
    }

    @Named("bookFromId")
    default Book bookFromId(Long id) {
        Book book = new Book();
        book.setId(id);
        return book;
    }
}
