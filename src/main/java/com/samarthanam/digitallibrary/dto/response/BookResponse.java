package com.samarthanam.digitallibrary.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookResponse {

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
    private String totalAudioTime;
    private String fileName;
    private String thumbnailUrl;

}