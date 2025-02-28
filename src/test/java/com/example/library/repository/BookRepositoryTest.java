package com.example.library.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.config.CustomMySqlContainer;
import com.example.library.model.Book;
import com.example.library.model.Category;
import com.example.library.repository.book.BookRepository;
import com.example.library.repository.category.CategoryRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

    private static CustomMySqlContainer mySqlContainer = CustomMySqlContainer.getInstance();

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Find all books by category ID - should return books belonging to category")
    void findAllByCategoryId_CategoryWithBooks_ReturnsBooks() {
        Category category = new Category()
                .setName("Fiction")
                .setDescription("Fiction books");

        categoryRepository.save(category);

        Book book = new Book()
                .setTitle("Sample Book 1")
                .setAuthor("Author B")
                .setIsbn("0-306-40615-2")
                .setPrice(BigDecimal.valueOf(149.99))
                .setDescription("Another sample book description.")
                .setCoverImage("http://example.com/cover1.jpg")
                .setCategories(Set.of(category));

        bookRepository.save(book);

        List<Book> result = bookRepository.findAllByCategoryId(category.getId());

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("Sample Book 1");
    }
}
