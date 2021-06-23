package com.samarthanam.digitallibrary.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookResponse {

    private String authorName;
    private String authorId;
    private Integer isbn;
    private String categoryName;
    private String categoryId;
    private String bookType;
    private String title;
    private String year;
    private String countryOfOrigin;
    private String editionVersion;
    private LocalDateTime updatedTimestamp;
    private LocalDateTime createdTimestamp;
    private Integer totalPages;
    private String totalAudioTime;
    private String fileName;
    private String thumbnailUrl;

}