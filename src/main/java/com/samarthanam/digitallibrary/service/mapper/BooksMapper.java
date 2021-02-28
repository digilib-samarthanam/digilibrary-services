package com.samarthanam.digitallibrary.service.mapper;

import com.samarthanam.digitallibrary.dto.response.Book;
import com.samarthanam.digitallibrary.dto.response.BookActivityStatus;
import com.samarthanam.digitallibrary.entity.UserActivityHistory;
import com.samarthanam.digitallibrary.entity.UserBookmarks;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BooksMapper {

    @Mapping(target = "author", source = "author.firstName")
    @Mapping(target = "category", source = "category.categoryName")
    @Mapping(target = "bookType", source = "bookType.bookTypeDescription")
    @Mapping(target = "thumbnailUrl", ignore = true)
    Book map(com.samarthanam.digitallibrary.entity.Book book);

    List<Book> mapToBooks(List<com.samarthanam.digitallibrary.entity.Book> books);

    BookActivityStatus map(UserActivityHistory userActivityHistory);
    BookActivityStatus map(UserBookmarks userBookmarks);
    List<BookActivityStatus> mapUserActivityHistoryToBookActivityStatuses(List<UserActivityHistory> books);
    List<BookActivityStatus> mapUserBookmarksToBookActivityStatuses(List<UserBookmarks> books);

}