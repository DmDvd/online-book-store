package com.example.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.library.dto.category.CategoryDto;
import com.example.library.dto.category.CreateCategoryRequestDto;
import com.example.library.mapper.CategoryMapper;
import com.example.library.model.Category;
import com.example.library.repository.category.CategoryRepository;
import com.example.library.service.category.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Save category with valid request should return CategoryDto")
    void save_ValidCreateCategoryDto_ReturnsCategoryDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto()
                .setName("Fiction")
                .setDescription("Fiction books");

        Category category = new Category()
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());

        CategoryDto categoryDto = new CategoryDto()
                .setId(1L)
                .setName(category.getName())
                .setDescription(category.getDescription());

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.save(requestDto);

        assertThat(result).isEqualTo(categoryDto);
        verify(categoryMapper, times(1)).toEntity(requestDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Get category by valid ID should return correct category")
    void getById_WithValidCategoryById_ShouldReturnValidCategory() {
        Long categoryId = 1L;

        Category category = new Category()
                .setId(categoryId)
                .setName("Fiction")
                .setDescription("Fiction book");

        CategoryDto categoryDto = new CategoryDto()
                .setId(categoryId)
                .setName("Fiction")
                .setDescription("Fiction book");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.getById(categoryId);

        assertNotNull(result);
        assertEquals(categoryDto.getName(), result.getName());
        assertEquals(categoryDto.getDescription(), result.getDescription());
    }

    @Test
    @DisplayName("Get category by non-existing ID should throw exception")
    void getById_WithNonExistingCategoryId_ShouldThrowException() {
        Long categoryId = 100L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                RuntimeException.class, () -> categoryService.getById(categoryId)
        );

        String expected = "Can't find category by id " + categoryId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete category by valid ID should delete category")
    void deleteById_ValidDeleteCategory_DeleteCategory() {
        Long categoryId = 1L;

        doNothing().when(categoryRepository).deleteById(categoryId);

        categoryService.deleteById(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Find all categories with valid pageable should return category list")
    void findAll_ValidPageable_ReturnsAllCategory() {
        Category category = new Category()
                .setId(1L)
                .setName("Fiction")
                .setDescription("Fiction book");

        CategoryDto categoryDto = new CategoryDto()
                .setId(category.getId())
                .setName(category.getName())
                .setDescription(category.getDescription());

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        Page<CategoryDto> categoryDtos = categoryService.findAll(pageable);

        assertThat(categoryDtos).hasSize(1);
        assertThat(categoryDtos.getContent()).containsExactly(categoryDto);

        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("Update category with valid data should update category")
    void update_ValidUpdateCategory_UpdateCategory() {
        Long categoryId = 1L;
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto()
                .setName("Fiction")
                .setDescription("Fiction books");

        Category category = new Category()
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());

        CategoryDto categoryDto = new CategoryDto()
                .setId(categoryId)
                .setName(category.getName())
                .setDescription(category.getDescription());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doNothing().when(categoryMapper).updateCategoryFromDto(requestDto, category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto updateCategory = categoryService.update(categoryId, requestDto);

        assertThat(updateCategory).isEqualTo(categoryDto);
    }
}
