package com.example.library;

import com.example.library.config.CustomMySqlContainer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class LibraryApplicationTests {

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

    @DynamicPropertySource
    static void configureProperties(@NotNull DynamicPropertyRegistry registry) {
        registry.add(SPRING_URL, mysql::getJdbcUrl);
        registry.add(SPRING_USERNAME, mysql::getUsername);
        registry.add(SPRING_PASSWORD, mysql::getPassword);
        registry.add(SPRING_DRIVER_CLASS, mysql::getDriverClassName);
    }

    @Test
    void contextLoads() {
    }
}
