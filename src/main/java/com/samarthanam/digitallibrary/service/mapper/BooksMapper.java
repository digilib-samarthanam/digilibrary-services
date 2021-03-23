package com.samarthanam.digitallibrary.service.mapper;

import com.samarthanam.digitallibrary.dto.response.BookResponse;
import com.samarthanam.digitallibrary.dto.response.BookActivityStatus;
import com.samarthanam.digitallibrary.entity.UserActivityHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BooksMapper {

    @Mapping(target = "author", source = "author.firstName")
    @Mapping(target = "category", source = "category.categoryName")
    @Mapping(target = "bookType", source = "bookType.bookTypeDescription")
    BookResponse mapToBook(com.samarthanam.digitallibrary.entity.Book book);

    List<BookResponse> mapToBooks(List<com.samarthanam.digitallibrary.entity.Book> books);

    BookActivityStatus mapToBookActivityStatus(UserActivityHistory userActivityHistory);
    List<BookActivityStatus> mapToBookActivityStatuses(List<UserActivityHistory> books);

}