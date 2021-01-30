package com.samarthanam.digitallibrary.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.samarthanam.digitallibrary.entity.Book;
import com.samarthanam.digitallibrary.entity.UserActivityHistory;
import com.samarthanam.digitallibrary.entity.UserBookmarks;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class HomePageResponse {

    List<UserActivityHistory> recentlyViewedBooks;
    List<UserBookmarks> bookmarkedBooks;
    List<Book> recentlyAddedBooks;

}
