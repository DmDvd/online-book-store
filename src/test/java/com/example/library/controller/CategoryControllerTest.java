package com.example.library.controller;

import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.config.CustomMySqlContainer;
import com.example.library.config.PagedCategoryResponse;
import com.example.library.dto.book.BookDtoWithoutCategoryIds;
import com.example.library.dto.category.CategoryDto;
import com.example.library.dto.category.CreateCategoryRequestDto;
import com.example.library.service.category.CategoryService;
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
import org.springframework.data.domain.Page;
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
public class CategoryControllerTest {

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
    private CategoryService categoryService;

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
    @DisplayName("Create a new category - should return created category")
    void createCategory_ValidRequestDto_Success() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto()
                .setName("Fiction")
                .setDescription("Fiction books");

        CategoryDto expected = new CategoryDto()
                .setId(1L)
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());
        Mockito.when(categoryService
                .save(Mockito.any(CreateCategoryRequestDto.class)))
                .thenReturn(expected);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get category by ID - should return category details")
    void getCategoryById_GivenCategoryById_ShouldReturnCategory() throws Exception {
        Long id = 1L;
        CategoryDto expected = new CategoryDto()
                .setId(id)
                .setName("Fiction")
                .setDescription("Fiction book");

        Mockito.when(categoryService.getById(id)).thenReturn(expected);
        MvcResult result = mockMvc.perform(get("/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(content, CategoryDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete category by ID - should return no content")
    void deleteCategory_DeleteCategoryById_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/categories/{id}", 1))
                .andExpect(status().isNoContent());
        Mockito.verify(categoryService, times(1)).deleteById(1L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update category - should return updated category")
    void updateCategory_ValidUpdateCategory_ShouldReturnCategory() throws Exception {
        Long id = 1L;
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto()
                .setName("Fiction")
                .setDescription("Fiction books");

        CategoryDto expected = new CategoryDto()
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());

        Mockito.when(categoryService.update(id, requestDto)).thenReturn(expected);

        MvcResult result = mockMvc.perform(put("/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(content, CategoryDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
    }

    @WithMockUser
    @Test
    @DisplayName("Get books by category ID - should return list of books")
    void getBooksByCategoryId_ValidId_ShouldReturnBooksList() throws Exception {
        Long categoryId = 1L;
        List<BookDtoWithoutCategoryIds> expected = List.of(
                new BookDtoWithoutCategoryIds()
                        .setId(1L)
                        .setTitle("Sample Book 1")
                        .setAuthor("Author A")
                        .setDescription("Description A")
                        .setPrice(BigDecimal.valueOf(149.99)),
                new BookDtoWithoutCategoryIds()
                        .setId(2L)
                        .setTitle("Sample Book 2")
                        .setAuthor("Author B")
                        .setDescription("Description B")
                        .setPrice(BigDecimal.valueOf(249.99))
        );

        Mockito.when(categoryService.getBooksByCategoryId(categoryId)).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/categories/{id}/books",
                        categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds[].class
        );
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected.getFirst().getId(), actual[0].getId());
    }

    @WithMockUser
    @Test
    @DisplayName("Get all categories - should return paginated list")
    void getAllCategories_ShouldReturnPagedCategories() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<CategoryDto> categoryList = List.of(
                new CategoryDto()
                        .setId(1L)
                        .setName("Fiction")
                        .setDescription("Description fiction"),
                new CategoryDto()
                        .setId(2L)
                        .setName("Science")
                        .setDescription("Description science"),
                new CategoryDto()
                        .setId(3L)
                        .setName("History")
                        .setDescription("Description history")
        );
        Page<CategoryDto> expected = new PageImpl<>(categoryList, pageable, categoryList.size());

        Mockito.when(categoryService.findAll(Mockito.any(Pageable.class)))
                .thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/categories")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PagedCategoryResponse actual = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                PagedCategoryResponse.class);
        Assertions.assertEquals(expected.getContent().size(), actual.getContent().size());
    }
}
