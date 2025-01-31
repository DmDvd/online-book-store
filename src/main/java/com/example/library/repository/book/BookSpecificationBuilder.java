package com.example.library.repository.book;

import static com.example.library.repository.book.AuthorSpecificationProvider.AUTHOR;
import static com.example.library.repository.book.IsbnSpecificationProvider.ISBN;
import static com.example.library.repository.book.TitleSpecificationProvider.TITLE;

import com.example.library.dto.book.BookSearchParametersDto;
import com.example.library.model.Book;
import com.example.library.repository.SpecificationBuilder;
import com.example.library.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParametersDto) {
        Specification<Book> spec = Specification.where(null);
        if (searchParametersDto.title() != null && searchParametersDto.title().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(TITLE)
                    .getSpecification(searchParametersDto.title()));
        }
        if (searchParametersDto.author() != null && searchParametersDto.author().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(AUTHOR)
                    .getSpecification(searchParametersDto.author()));
        }
        if (searchParametersDto.isbn() != null && searchParametersDto.isbn().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(ISBN)
                    .getSpecification(searchParametersDto.isbn()));
        }
        return spec;
    }
}
