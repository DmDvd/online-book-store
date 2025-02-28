package com.example.library.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.config.PagedCategoryResponse;
import com.example.library.config.TestUtil;
import com.example.library.dto.book.BookDtoWithoutCategoryIds;
import com.example.library.dto.category.CategoryDto;
import com.example.library.dto.category.CreateCategoryRequestDto;
import com.example.library.exception.EntityNotFoundException;
import com.example.library.service.category.CategoryService;
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
import org.springframework.data.domain.Page;
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
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith(MockitoExtension.class)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private WebApplicationContext applicationContext;

    @BeforeEach
    void beforeEach() throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new category - should return created category")
    void createCategory_ValidRequestDto_Success() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createCategoryRequestDto();

        CategoryDto expected = TestUtil.createCategoryDto(1L);
        when(categoryService
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
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

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

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get category by ID - should return category details")
    void getCategoryById_GivenCategoryById_ShouldReturnCategory() throws Exception {
        Long id = 1L;
        CategoryDto expected = TestUtil.createCategoryDto(id);

        when(categoryService.getById(id)).thenReturn(expected);
        MvcResult result = mockMvc.perform(get("/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(content, CategoryDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Searching for a category by a non-existent ID should return 404 Not Found")
    void getCategoryById_GivenCategoryByInvalidId_ShouldReturnNotFound() throws Exception {
        Long invalidId = 100L;
        when(categoryService.getById(invalidId))
                .thenThrow(new EntityNotFoundException("Category not found"));

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
        verify(categoryService, times(1)).deleteById(1L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update category - should return updated category")
    void updateCategory_ValidUpdateCategory_ShouldReturnCategory() throws Exception {
        Long id = 1L;
        CreateCategoryRequestDto requestDto = TestUtil.createCategoryRequestDto();

        CategoryDto expected = TestUtil.createCategoryDto(id);

        when(categoryService.update(id, requestDto)).thenReturn(expected);

        MvcResult result = mockMvc.perform(put("/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        CategoryDto actual = objectMapper.readValue(content, CategoryDto.class);

        assertNotNull(actual);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Updating a non-existent category should return 404 Not Found")
    void updateCategory_NonExistentId_ShouldReturnNotFound() throws Exception {
        Long id = 100L;
        CreateCategoryRequestDto validDto = TestUtil.createCategoryRequestDto();
        when(categoryService.update(id, validDto))
                .thenThrow(new EntityNotFoundException("Category with id " + id + " not found"));

        String jsonRequest = objectMapper.writeValueAsString(validDto);
        MvcResult result = mockMvc.perform(put("/categories/{id}", id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
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

        when(categoryService.getBooksByCategoryId(categoryId)).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/categories/{id}/books",
                        categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds[].class
        );
        assertNotNull(actual);
        assertEquals(expected.size(), actual.length);
        assertEquals(expected.getFirst().getId(), actual[0].getId());
    }

    @WithMockUser
    @Test
    @DisplayName("Get all categories - should return paginated list")
    void getAllCategories_ShouldReturnPagedCategories() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<CategoryDto> categoryList = List.of(
                TestUtil.createCategoryDto(1L),
                TestUtil.createCategoryDto(2L),
                TestUtil.createCategoryDto(3L)
        );
        Page<CategoryDto> expected = new PageImpl<>(categoryList, pageable, categoryList.size());

        when(categoryService.findAll(Mockito.any(Pageable.class)))
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
        assertEquals(expected.getContent().size(), actual.getContent().size());
    }
}
