package com.samarthanam.digitallibrary.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryResponseDto {
    private Integer categoryId;

    private String categoryName;

    private LocalDateTime createdTimestamp;

    private long booksCount;
}
