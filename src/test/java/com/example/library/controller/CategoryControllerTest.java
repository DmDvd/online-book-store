package com.example.library.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.config.CustomPageImpl;
import com.example.library.config.TestUtil;
import com.example.library.dto.book.BookDto;
import com.example.library.dto.category.CategoryDto;
import com.example.library.dto.category.CreateCategoryRequestDto;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class CategoryControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext applicationContext;

    @BeforeEach
    void beforeEach(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Sql(scripts = "classpath:database/books/category/delete-all-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new category - should return created category")
    void createCategory_ValidRequestDto_Success() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createCategoryRequestDto();

        CategoryDto expected = TestUtil.createCategoryDto(1L);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Sql(scripts = "classpath:database/books/category/delete-all-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Creating a category with invalid data should return a 400 Bad Request")
    void createCategory_InvalidRequestDto_ShouldReturnBadRequest() throws Exception {
        CreateCategoryRequestDto invalidRequest = new CreateCategoryRequestDto()
                .setName("")
                .setDescription("");
        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);

        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Sql(scripts = "classpath:database/books/category/add-one-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/category/delete-all-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get category by ID - should return category details")
    void getCategoryById_GivenCategoryById_ShouldReturnCategory() throws Exception {

        MvcResult result = mockMvc.perform(get("/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(content, CategoryDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
    }

    @Sql(scripts = "classpath:database/books/category/add-one-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/category/delete-all-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Searching for a category by a non-existent ID should return 404 Not Found")
    void getCategoryById_GivenCategoryByInvalidId_ShouldReturnNotFound() throws Exception {
        Long invalidId = 100L;

        mockMvc.perform(get("/categories/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete category by ID - should return no content")
    void deleteCategory_DeleteCategoryById_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/categories/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Sql(scripts = "classpath:database/books/category/add-one-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/category/delete-all-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update category - should return updated category")
    void updateCategory_ValidUpdateCategory_ShouldReturnCategory() throws Exception {
        Long id = 1L;
        CreateCategoryRequestDto expected = TestUtil.createCategoryRequestDto()
                .setName("Fiction");

        MvcResult result = mockMvc.perform(put("/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expected))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(content, CategoryDto.class);

        assertNotNull(actual);
        assertEquals(expected.getName(), actual.getName());
    }

    @Sql(scripts = "classpath:database/books/category/add-one-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/category/delete-all-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Updating a non-existent category should return 404 Not Found")
    void updateCategory_NonExistentId_ShouldReturnNotFound() throws Exception {
        Long id = 100L;
        CreateCategoryRequestDto validDto = TestUtil.createCategoryRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(validDto);
        MvcResult result = mockMvc.perform(put("/categories/{id}", id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

    }

    @Sql(scripts = "classpath:database/books/category/add-one-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/category/delete-all-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser
    @Test
    @DisplayName("Get books by category ID - should return list of books")
    void getBooksByCategoryId_ValidId_ShouldReturnBooksList() throws Exception {
        Long categoryId = 1L;

        mockMvc.perform(get("/categories/{id}/books",
                        categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Sql(scripts = "classpath:database/books/category/add-two-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/category/delete-all-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser
    @Test
    @DisplayName("Get all categories - should return paginated list")
    void getAllCategories_ShouldReturnPagedCategories() throws Exception {

        MvcResult result = mockMvc.perform(get("/categories")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JavaType type = objectMapper.getTypeFactory()
                .constructParametricType(CustomPageImpl.class, CategoryDto.class);
        PageImpl<BookDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), type
        );
        assertNotNull(actual);
        assertFalse(actual.getContent().isEmpty());
    }
}
