package com.samarthanam.digitallibrary.dto.response;

import com.samarthanam.digitallibrary.entity.Category;
import lombok.Data;


import java.time.LocalDateTime;
@Data
public class SubCategoryResponseDto {
    private Integer subCategoryId;

    private Category category;

    private String subCategoryName;

    private LocalDateTime createdTimestamp;

    private Long booksCount;
}
