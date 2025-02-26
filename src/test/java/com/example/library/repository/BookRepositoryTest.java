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
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

    private static final String DB_NAME = "books";
    private static final String TEST_USER = "test";
    private static final String TEST_PASSWORD = "test";
    private static final String SPRING_URL = "spring.datasource.url";
    private static final String SPRING_USERNAME = "spring.datasource.username";
    private static final String SPRING_PASSWORD = "spring.datasource.password";
    private static final String SPRING_DRIVER_CLASS = "spring.datasource.driver-class-name";

    @Container
    private static final CustomMySqlContainer mysql = CustomMySqlContainer.getInstance()
            .withDatabaseName(DB_NAME)
            .withUsername(TEST_USER)
            .withPassword(TEST_PASSWORD);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @DynamicPropertySource
    static void configureProperties(@NotNull DynamicPropertyRegistry registry) {
        registry.add(SPRING_URL, mysql::getJdbcUrl);
        registry.add(SPRING_USERNAME, mysql::getUsername);
        registry.add(SPRING_PASSWORD, mysql::getPassword);
        registry.add(SPRING_DRIVER_CLASS, mysql::getDriverClassName);
    }

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
