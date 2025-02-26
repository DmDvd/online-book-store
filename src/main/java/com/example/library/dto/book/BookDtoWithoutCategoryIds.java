package com.example.library.dto.book;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class BookDtoWithoutCategoryIds {
    private Long id;
    private String title;
    private String author;
    private String description;
    private BigDecimal price;
}
