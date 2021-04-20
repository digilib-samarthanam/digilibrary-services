package com.samarthanam.digitallibrary.service.mapper;

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

    @Mapping(target = "author", expression = "java(book.getAuthor().getFirstName() + \" \" + book.getAuthor().getLastName())")
    @Mapping(target = "category", source = "category.categoryName")
    @Mapping(target = "bookType", expression = "java(book.getBookTypeFormat().getBookTypeDescription().toString())")
    @Mapping(target = "thumbnailUrl", ignore = true)
    BookResponse map(Book book);

    BookActivityStatus map(UserActivityHistory userActivityHistory);

    BookActivityStatus map(UserBookmarks userBookmarks);

    @Mapping(target = "book.isbn", source = "isbn")
    UserBookmarks mapWithNoCurrentTimestamp(BookActivityStatusRequest bookActivityStatusRequest);

    @Mapping(target = "book.isbn", source = "isbn")
    @Mapping(target = "createdTimestamp", expression = "java(java.time.LocalDateTime.now(com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE))")
    UserBookmarks mapWithCurrentTimestamp(BookActivityStatusRequest bookActivityStatusRequest);

}