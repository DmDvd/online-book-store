package com.example.library.mapper;

import com.example.library.config.MapperConfig;
import com.example.library.dto.book.BookDto;
import com.example.library.dto.book.BookDtoWithoutCategoryIds;
import com.example.library.dto.book.CreateBookRequestDto;
import com.example.library.dto.category.CategoryDto;
import com.example.library.model.Book;
import com.example.library.model.Category;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {

    BookDto toDto(Book book);

    @Mapping(target = "categories", source = "categoriesId")
    Book toModel(CreateBookRequestDto requestDto);

    void updateBookFromDto(CreateBookRequestDto requestDto, @MappingTarget Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        if (book.getCategories() != null) {
            Set<CategoryDto> categoryDtos = book.getCategories().stream()
                    .map(category -> new CategoryDto()
                            .setId(category.getId())
                            .setName(category.getName())
                            .setDescription(category.getDescription()))
                    .collect(Collectors.toSet());

            bookDto.setCategories(categoryDtos);
        }
    }

    default Set<Category> map(List<Long> categoryIds) {
        if (categoryIds == null) {
            return new HashSet<>();
        }
        return categoryIds.stream()
                .map(id -> new Category().setId(id))
                .collect(Collectors.toSet());
    }
}
