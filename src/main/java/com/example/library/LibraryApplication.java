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
                Book greenKing = new Book();
                greenKing.setTitle("Green King");
                greenKing.setAuthor("Paul-Loup Sulitzer");
                greenKing.setIsbn(" 978-985-15-4656-1");
                greenKing.setPrice(BigDecimal.valueOf(320));
                greenKing.setDescription("Titan is a novel by the American writer "
                        + "Theodore Dreiser, published in May 1914.");

                bookService.save(greenKing);

                System.out.println(bookService.findAll());
            }
        };
    }
}
