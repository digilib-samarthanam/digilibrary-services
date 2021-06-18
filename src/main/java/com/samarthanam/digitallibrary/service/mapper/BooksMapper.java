package com.samarthanam.digitallibrary.service.mapper;

import com.samarthanam.digitallibrary.dto.request.BookCreateRequest;
import com.samarthanam.digitallibrary.dto.response.BookActivityStatusRequest;
import com.samarthanam.digitallibrary.dto.response.BookResponse;
import com.samarthanam.digitallibrary.dto.response.BookActivityStatus;
import com.samarthanam.digitallibrary.entity.Book;
import com.samarthanam.digitallibrary.entity.UserActivityHistory;
import com.samarthanam.digitallibrary.entity.UserBookmarks;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BooksMapper {

    @Mapping(target = "author.authorId", source = "authorId")
    @Mapping(target = "author.name", source = "authorName")
    @Mapping(target = "category.categoryId", source = "categoryId")
    @Mapping(target = "createdTimestamp", expression = "java(java.time.LocalDateTime.now(com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE))")
    @Mapping(target = "updatedTimestamp", expression = "java(java.time.LocalDateTime.now(com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE))")
    @Mapping(target = "bookTypeFormat", ignore = true)
    @Mapping(target = "active", constant = "true")
    Book map(BookCreateRequest bookCreateRequest);

    @Mapping(target = "author", source = "author.name")
    @Mapping(target = "category", source = "category.categoryName")
    @Mapping(target = "bookType", expression = "java(book.getBookTypeFormat().getBookTypeDescription().toString())")
    @Mapping(target = "thumbnailUrl", ignore = true)
    BookResponse map(Book book);


    @Mapping(target = "updatedTimestamp", ignore = true)
    BookActivityStatus map(UserActivityHistory userActivityHistory);

    BookActivityStatus map(UserBookmarks userBookmarks);

    @Mapping(target = "book.isbn", source = "isbn")
    @Mapping(target = "createdTimestamp", expression = "java(java.time.LocalDateTime.now(com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE))")
    @Mapping(target = "userBookmarksId", ignore = true)
    UserBookmarks mapToUserBookmark(BookActivityStatusRequest bookActivityStatusRequest);

    @Mapping(target = "book.isbn", source = "isbn")
    @Mapping(target = "createdTimestamp", expression = "java(java.time.LocalDateTime.now(com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE))")
    @Mapping(target = "updatedTimestamp", expression = "java(java.time.LocalDateTime.now(com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE))")
    @Mapping(target = "userActivityHistoryId", ignore = true)
    UserActivityHistory mapToUserActivityHistory(BookActivityStatusRequest bookActivityStatusRequest);

}