package com.samarthanam.digitallibrary.dto.request;

import com.samarthanam.digitallibrary.constant.BookType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class BookRequest {

    @NotBlank
    private String authorName;

    @Positive
    @NotNull
    private Integer isbn;

    @NotBlank
    private String categoryName;

    @NotNull
    private BookType bookType;

    @NotBlank
    private String title;

    private String year;
    private String countryOfOrigin;
    private String editionVersion;

    @Positive
    private Integer totalPages;

    private String totalAudioTime;

    @NotBlank
    private String fileName;

}