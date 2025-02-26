package com.example.library.config;

import com.example.library.dto.category.CategoryDto;
import java.util.List;
import lombok.Data;

@Data
public class PagedCategoryResponse {
    private List<CategoryDto> content;
    private int totalElements;
    private int totalPages;
}
