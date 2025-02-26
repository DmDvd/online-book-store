package com.example.library.controller;

import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.config.CustomMySqlContainer;
import com.example.library.config.PagedBookResponse;
import com.example.library.dto.book.BookDto;
import com.example.library.dto.book.BookSearchParametersDto;
import com.example.library.dto.book.CreateBookRequestDto;
import com.example.library.service.book.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith(MockitoExtension.class)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    protected static MockMvc mockMvc;

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
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @DynamicPropertySource
    static void configureProperties(@NotNull DynamicPropertyRegistry registry) {
        registry.add(SPRING_URL, mysql::getJdbcUrl);
        registry.add(SPRING_USERNAME, mysql::getUsername);
        registry.add(SPRING_PASSWORD, mysql::getPassword);
        registry.add(SPRING_DRIVER_CLASS, mysql::getDriverClassName);
    }

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new book - should return created book")
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Sample Book 1")
                .setAuthor("Author B")
                .setIsbn("0-306-40615-2")
                .setPrice(BigDecimal.valueOf(149.99))
                .setDescription("Another sample book description.")
                .setCoverImage("http://example.com/cover1.jpg")
                .setCategoriesId(List.of(1L));

        BookDto expected = new BookDto()
                .setId(1L)
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage())
                .setCategoryIds(requestDto.getCategoriesId());

        Mockito.when(bookService.createBook(Mockito.any(CreateBookRequestDto.class)))
                .thenReturn(expected);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = BookControllerTest.mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getPrice(), actual.getPrice());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all books - should return paginated list of books")
    void getAll_GivenBookInCatalog_ShouldReturnAllBooks() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookDto> expected = List.of(
                new BookDto()
                        .setId(1L)
                        .setTitle("Sample Book 1")
                        .setAuthor("Author A")
                        .setIsbn("0-306-40615-2")
                        .setPrice(BigDecimal.valueOf(149.99))
                        .setDescription("Another sample book description A")
                        .setCoverImage("http://example.com/cover1.jpg")
                        .setCategoryIds(List.of(1L, 2L)),

                new BookDto()
                        .setId(2L)
                        .setTitle("Sample Book 2")
                        .setAuthor("Author B")
                        .setIsbn("0-405-50617-3")
                        .setPrice(BigDecimal.valueOf(249.99))
                        .setDescription("Another sample book description B")
                        .setCoverImage("http://example.com/cover2.jpg")
                        .setCategoryIds(List.of(1L, 2L))
        );
        PageImpl<BookDto> bookDtos = new PageImpl<>(expected, pageable, expected.size());

        Mockito.when(bookService.getAll(Mockito.any(Pageable.class))).thenReturn(bookDtos);

        MvcResult result = BookControllerTest.mockMvc.perform(
                get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10")
        )
                .andExpect(status().isOk())
                .andReturn();

        PagedBookResponse actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                PagedBookResponse.class
        );
        Assertions.assertEquals(2, actual.getContent().size());
        Assertions.assertEquals(expected, actual.getContent());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Find book by ID - should return book details")
    void findById_GivenBookInCatalogById_ShouldReturnBook() throws Exception {

        BookDto expected = new BookDto()
                .setId(1L)
                .setTitle("Sample Book 1")
                .setAuthor("Author A")
                .setIsbn("0-306-40615-2")
                .setPrice(BigDecimal.valueOf(149.99))
                .setDescription("Another sample book description A")
                .setCoverImage("http://example.com/cover1.jpg")
                .setCategoryIds(List.of(1L, 2L));

        Mockito.when(bookService.getBookById(1L)).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        BookDto actual = objectMapper.readValue(content, BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete book by ID - should return no content")
    void delete_DeleteBookById_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/books/{id}", 1))
                .andExpect(status().isNoContent());

        Mockito.verify(bookService, times(1)).deleteById(1L);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update book by ID - should return updated book")
    void updateBook_ValidUpdateBook_ShouldReturnUpdateBook() throws Exception {
        Long id = 1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Sample Book 1")
                .setAuthor("Author B")
                .setIsbn("0-306-40615-2")
                .setPrice(BigDecimal.valueOf(149.99))
                .setDescription("Another sample book description.")
                .setCoverImage("http://example.com/cover1.jpg")
                .setCategoriesId(List.of(1L));

        BookDto expected = new BookDto()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage())
                .setCategoryIds(requestDto.getCategoriesId());

        Mockito.when(bookService.updateBook(id, requestDto)).thenReturn(expected);

        MvcResult result = mockMvc.perform(put("/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        BookDto actual = objectMapper.readValue(content, BookDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getAuthor(), actual.getAuthor());
        Mockito.verify(bookService).updateBook(id, requestDto);
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Search books with parameters - should return matching books")
    void searchBooks_ValidSearchParameters_ReturnsBookDtos() throws Exception {
        List<BookDto> expected = List.of(
                new BookDto()
                        .setId(1L)
                        .setTitle("Sample Book 1")
                        .setAuthor("Author B")
                        .setIsbn("0-306-40615-2")
                        .setPrice(BigDecimal.valueOf(149.99))
                        .setDescription("Another sample book description A")
                        .setCoverImage("http://example.com/cover1.jpg")
                        .setCategoryIds(List.of(1L, 2L))
        );

        Mockito.when(bookService
                        .search(Mockito.any(BookSearchParametersDto.class)))
                .thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("title", "Sample Book 1")
                        .param("author", "Author B")
                        .param("isbn", "0-306-40615-2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDto[].class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected.getFirst().getTitle(), actual[0].getTitle());
    }
}
