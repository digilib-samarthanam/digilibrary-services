package com.samarthanam.digitallibrary.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Book {

    private String author;
    private Integer isbn;
    private String category;
    private String bookType;
    private String title;
    private String year;
    private String countryOfOrigin;
    private String editionVersion;
    private LocalDateTime updatedTimestamp;
    private LocalDateTime createdTimestamp;
    private Integer totalPages;
    private String thumbnailUrl;

}