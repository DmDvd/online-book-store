package com.example.library.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.config.PagedBookResponse;
import com.example.library.config.TestUtil;
import com.example.library.dto.book.BookDto;
import com.example.library.dto.book.BookSearchParametersDto;
import com.example.library.dto.book.CreateBookRequestDto;
import com.example.library.exception.EntityNotFoundException;
import com.example.library.service.book.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private WebApplicationContext applicationContext;

    @BeforeEach
    void beforeAll() throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new book - should return created book")
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();

        BookDto expected = TestUtil.createBookDto(1L);

        when(bookService.createBook(Mockito.any(CreateBookRequestDto.class)))
                .thenReturn(expected);

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

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all books - should return paginated list of books")
    void getAll_GivenBookInCatalog_ShouldReturnAllBooks() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookDto> expected = List.of(
                TestUtil.createBookDto(1L),
                TestUtil.createBookDto(2L)
        );
        PageImpl<BookDto> bookDtos = new PageImpl<>(expected, pageable, expected.size());

        when(bookService.getAll(Mockito.any(Pageable.class))).thenReturn(bookDtos);

        MvcResult result = mockMvc.perform(
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
        assertEquals(2, actual.getContent().size());
        assertEquals(expected, actual.getContent());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Find book by ID - should return book details")
    void findById_GivenBookInCatalogById_ShouldReturnBook() throws Exception {

        BookDto expected = TestUtil.createBookDto(1L);

        when(bookService.getBookById(1L)).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        BookDto actual = objectMapper.readValue(content, BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Searching for a book by a non-existent ID should return 404 Not Found")
    void findById_BookByInvalidId_ShouldReturnNotFound() throws Exception {
        Long invalidId = 100L;
        when(bookService.getBookById(invalidId))
                .thenThrow(new EntityNotFoundException("Book not found"));

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

        verify(bookService).deleteById(1L);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update book by ID - should return updated book")
    void updateBook_ValidUpdateBook_ShouldReturnUpdateBook() throws Exception {
        Long id = 1L;
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();

        BookDto expected = TestUtil.createBookDto(1L);

        when(bookService.updateBook(id, requestDto)).thenReturn(expected);

        MvcResult result = mockMvc.perform(put("/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        BookDto actual = objectMapper.readValue(content, BookDto.class);

        assertNotNull(actual);
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getAuthor(), actual.getAuthor());
        verify(bookService).updateBook(id, requestDto);
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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Updating a non-existent book should return 404 Not Found")
    void updateBook_NonExistentId_ShouldReturnNotFound() throws Exception {
        Long id = 100L;
        CreateBookRequestDto validDto = TestUtil.createBookRequestDto();
        when(bookService.updateBook(id, validDto))
                .thenThrow(new EntityNotFoundException("Book with id " + id + " not found"));

        String jsonRequest = objectMapper.writeValueAsString(validDto);
        MvcResult result = mockMvc.perform(put("/books/{id}", id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Search books with parameters - should return matching books")
    void searchBooks_ValidSearchParameters_ReturnsBookDtos() throws Exception {
        List<BookDto> expected = List.of(
                TestUtil.createBookDto(1L)
        );

        when(bookService
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
        assertNotNull(actual);
        assertEquals(expected.size(), actual.length);
        assertEquals(expected.getFirst().getTitle(), actual[0].getTitle());
    }
}
