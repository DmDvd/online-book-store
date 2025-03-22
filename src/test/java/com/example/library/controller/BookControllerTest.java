package com.example.library.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.config.CustomPageImpl;
import com.example.library.config.TestUtil;
import com.example.library.dto.book.BookDto;
import com.example.library.dto.book.CreateBookRequestDto;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Sql(scripts = "classpath:database/books/category-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-all-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new book - should return created book")
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();

        BookDto expected = TestUtil.createBookDto(1L);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getPrice(), actual.getPrice());
    }

    @Sql(scripts = "classpath:database/books/delete-all-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Creating a book with invalid data should return a 400 Bad Request")
    void createBook_InvalidRequestDto_ShouldReturnBadRequest() throws Exception {
        CreateBookRequestDto invalidRequest = new CreateBookRequestDto()
                .setAuthor("Author A")
                .setTitle("")
                .setPrice(BigDecimal.valueOf(-10.00))
                .setDescription("")
                .setIsbn("0000")
                .setCoverImage("");
        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);

        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Sql(scripts = "classpath:database/books/add-two-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-all-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all books - should return paginated list of books")
    void getAll_GivenBookInCatalog_ShouldReturnAllBooks() throws Exception {

        MvcResult result = mockMvc.perform(
                get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10")
        )
                .andExpect(status().isOk())
                .andReturn();

        JavaType type = objectMapper.getTypeFactory()
                .constructParametricType(CustomPageImpl.class, BookDto.class);
        PageImpl<BookDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), type
        );
        assertNotNull(actual);
        assertFalse(actual.getContent().isEmpty());
    }

    @Sql(scripts = "classpath:database/books/add-one-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-all-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Find book by ID - should return book details")
    void findById_GivenBookInCatalogById_ShouldReturnBook() throws Exception {

        MvcResult result = mockMvc.perform(get("/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        BookDto actual = objectMapper.readValue(content, BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
    }

    @Sql(scripts = "classpath:database/books/add-one-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-all-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Searching for a book by a non-existent ID should return 404 Not Found")
    void findById_BookByInvalidId_ShouldReturnNotFound() throws Exception {
        Long invalidId = 100L;

        mockMvc.perform(get("/books/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete book by ID - should return no content")
    void delete_DeleteBookById_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/books/{id}", 1))
                .andExpect(status().isNoContent());

    }

    @Sql(scripts = "classpath:database/books/add-two-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-all-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update book by ID - should return updated book")
    void updateBook_ValidUpdateBook_ShouldReturnUpdatedBook() throws Exception {
        Long id = 1L;
        CreateBookRequestDto updatedBookDto = new CreateBookRequestDto()
                .setTitle("Updated Title")
                .setAuthor("Updated Author")
                .setIsbn("0-123-45678-9")
                .setPrice(BigDecimal.valueOf(199.99))
                .setDescription("Updated description")
                .setCoverImage("http://example.com/cover_updated.jpg")
                .setCategoriesId(List.of(1L, 2L));

        MvcResult result = mockMvc.perform(put("/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBookDto)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        BookDto actual = objectMapper.readValue(content, BookDto.class);

        assertNotNull(actual);
        assertEquals(updatedBookDto.getTitle(), actual.getTitle());
        assertEquals(updatedBookDto.getAuthor(), actual.getAuthor());
        assertEquals(updatedBookDto.getIsbn(), actual.getIsbn());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Updating a book with invalid data should return a 400 Bad Request")
    void updateBook_InvalidData_ShouldReturnBadRequest() throws Exception {
        Long id = 1L;
        CreateBookRequestDto invalidRequest = new CreateBookRequestDto()
                .setAuthor("Author A")
                .setTitle("")
                .setPrice(BigDecimal.valueOf(-10.00))
                .setDescription("Sample Description")
                .setIsbn("123456789")
                .setCoverImage("");

        BookDto result = TestUtil.createBookDto(1L);

        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(put("/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Sql(scripts = "classpath:database/books/add-one-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Updating a non-existent book should return 404 Not Found")
    void updateBook_NonExistentId_ShouldReturnNotFound() throws Exception {
        Long id = 100L;
        CreateBookRequestDto validDto = TestUtil.createBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(validDto);
        mockMvc.perform(put("/books/{id}", id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Sql(scripts = "classpath:database/books/add-one-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-all-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Search books with parameters - should return matching books")
    void searchBooks_ValidSearchParameters_ReturnsBookDtos() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();
        BookDto expected = TestUtil.createBookDto(1L);

        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("title", "Sample Book 1")
                        .param("author", "Author A")
                        .param("isbn", "0-306-40615-2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDto[].class);
        assertNotNull(actual);
        assertEquals(1, actual.length);
        assertEquals("Sample Book 1", actual[0].getTitle());
    }
}
