package com.example.library;

import com.example.library.model.Book;
import com.example.library.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LibraryApplication {

    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Book titan = new Book();
                titan.setTitle("Titan");
                titan.setAuthor("Theodore Dreiser");
                titan.setIsbn("978-966-03-7862-9");
                titan.setPrice(BigDecimal.valueOf(430));
                titan.setDescription("Titan is a novel by the American writer "
                        + "Theodore Dreiser, published in May 1914.");

                bookService.save(titan);

                System.out.println(bookService.findAll());
            }
        };
    }
}
