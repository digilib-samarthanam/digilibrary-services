package com.samarthanam.digitallibrary.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class HomePageResponse {

    List<BookActivityStatus> recentlyViewedBooks;
    List<Book> bookmarkedBooks;
    List<Book> recentlyAddedBooks;

}
