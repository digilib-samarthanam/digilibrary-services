package com.samarthanam.digitallibrary.service.mapper;

import com.samarthanam.digitallibrary.dto.request.BookRequest;
import com.samarthanam.digitallibrary.dto.response.*;
import com.samarthanam.digitallibrary.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BooksMapper {

    @Mapping(target = "updatedTimestamp", expression = "java(java.time.LocalDateTime.now(com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE))")
    @Mapping(target = "bookTypeFormat", ignore = true)
    Book map(BookRequest bookRequest);

    @Mapping(target = "authorId", source = "author.authorId")
    @Mapping(target = "authorName", source = "author.name")
    @Mapping(target = "categoryId", source = "category.categoryId")
    @Mapping(target = "categoryName", source = "category.categoryName")
    @Mapping(target = "subCategoryId", source = "subCategory.subCategoryId")
    @Mapping(target = "subCategoryName", source = "subCategory.subCategoryName")
    @Mapping(target = "bookType", expression = "java(book.getBookTypeFormat().getBookTypeDescription().toString())")
    @Mapping(target = "thumbnailUrl", ignore = true)
    BookResponse map(Book book);

    @Mapping(target = "subCategoryId", source = "subCategory.subCategoryId")
    @Mapping(target = "subCategoryName", source = "subCategory.subCategoryName")
    @Mapping(target = "booksCount", ignore = true)
    @Mapping(target = "createdTimestamp", expression = "java(java.time.LocalDateTime.now(com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE))")
    SubCategoryResponseDto map(SubCategory subCategory);

    @Mapping(target = "categoryId", source = "category.categoryId")
    @Mapping(target = "categoryName", source = "category.categoryName")
    @Mapping(target = "booksCount", ignore = true)
    @Mapping(target = "createdTimestamp", expression = "java(java.time.LocalDateTime.now(com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE))")
    CategoryResponseDto map(Category category);

    BookActivityStatus map(UserActivityHistory userActivityHistory);

    @Mapping(target = "updatedTimestamp", ignore = true)
    BookActivityStatus map(UserBookmarks userBookmarks);

    @Mapping(target = "book.isbn", source = "isbn")
    @Mapping(target = "createdTimestamp", expression = "java(java.time.LocalDateTime.now(com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE))")
    UserBookmarks mapToUserBookmark(BookActivityStatusRequest bookActivityStatusRequest);

    @Mapping(target = "book.isbn", source = "isbn")
    @Mapping(target = "createdTimestamp", expression = "java(java.time.LocalDateTime.now(com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE))")
    @Mapping(target = "updatedTimestamp", expression = "java(java.time.LocalDateTime.now(com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE))")
    UserActivityHistory mapToUserActivityHistory(BookActivityStatusRequest bookActivityStatusRequest);

}