package com.samarthanam.digitallibrary.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookResponse {

    private String author;
    private Integer isbn;
    private String category;
    private Integer bookStatusId;
    private String bookType;
    private String title;
    private String year;
    private String countryOfOrigin;
    private String editionVersion;
    private LocalDateTime updatedTimestamp;
    private LocalDateTime createdTimestamp;

}