package com.example.library.repository.book;

import com.example.library.model.Book;
import com.example.library.repository.SpecificationProvider;
import com.example.library.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(provider ->
                        (provider instanceof TitleSpecificationProvider
                                && TitleSpecificationProvider.TITLE.equals(key))
                                || (provider instanceof AuthorSpecificationProvider
                                && AuthorSpecificationProvider.AUTHOR.equals(key))
                                || (provider instanceof IsbnSpecificationProvider
                                && IsbnSpecificationProvider.ISBN.equals(key)))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Can't find correct specification provider for key "
                                + key));
    }
}
