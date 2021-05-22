package com.samarthanam.digitallibrary.dto.request;

import com.samarthanam.digitallibrary.constant.BookType;
import lombok.Data;

@Data
public class BookCreateRequest {

    private String authorName;
    private Integer authorId;
    private Integer isbn;
    private Integer categoryId;
    private BookType bookType;
    private String title;
    private String year;
    private String countryOfOrigin;
    private String editionVersion;
    private Integer totalPages;
    private String totalAudioTime;
    private String fileName;

}