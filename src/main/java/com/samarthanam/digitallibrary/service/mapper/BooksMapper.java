package com.samarthanam.digitallibrary.service.mapper;

import com.samarthanam.digitallibrary.dto.response.Book;
import com.samarthanam.digitallibrary.dto.response.BookActivityStatus;
import com.samarthanam.digitallibrary.entity.UserActivityHistory;
import com.samarthanam.digitallibrary.entity.UserBookmarks;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BooksMapper {

    @Mapping(target = "author", expression = "java(book.getAuthor().getFirstName() + \" \" + book.getAuthor().getLastName())")
    @Mapping(target = "category", source = "category.categoryName")
    @Mapping(target = "bookType", source = "bookTypeFormat.bookTypeDescription")
    @Mapping(target = "thumbnailUrl", ignore = true)
    Book map(com.samarthanam.digitallibrary.entity.Book book);

    BookActivityStatus map(UserActivityHistory userActivityHistory);

    BookActivityStatus map(UserBookmarks userBookmarks);

}