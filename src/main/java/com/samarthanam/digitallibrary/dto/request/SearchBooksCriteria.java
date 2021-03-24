package com.samarthanam.digitallibrary.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class SearchBooksCriteria {

    @NotBlank
    private String anyBook;

    @NotBlank
    private String bookName;

    @NotBlank
    private String category;

    @NotBlank
    private String author;

    @NotBlank
    private String bookType;


    public SearchBooksCriteria(String anyBook, String bookName, String category, String author, String bookType) {
        this.anyBook = anyBook;
        this.bookName = bookName;
        this.category = category;
        this.author = author;
        this.bookType = bookType;
    }
}
