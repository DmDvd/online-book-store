package com.example.library.mapper;

import com.example.library.config.MapperConfig;
import com.example.library.dto.BookDto;
import com.example.library.dto.CreateBookRequestDto;
import com.example.library.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);
}
