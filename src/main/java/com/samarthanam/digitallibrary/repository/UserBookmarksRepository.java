package com.samarthanam.digitallibrary.repository;

import com.samarthanam.digitallibrary.constant.BookType;
import com.samarthanam.digitallibrary.entity.UserBookmarks;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface UserBookmarksRepository extends JpaRepository<UserBookmarks, Integer> {

    @EntityGraph(attributePaths = { "book.author", "book.category", "book.bookTypeFormat" })
    List<UserBookmarks> findByUserIdOrderByCreatedTimestampDesc(Integer userId, Pageable pageRequest);

    @EntityGraph(attributePaths = { "book.author", "book.category", "book.bookTypeFormat" })
    List<UserBookmarks> findByUserIdAndBookBookTypeFormatBookTypeDescriptionOrderByCreatedTimestampDesc(Integer userId, BookType bookType, Pageable pageRequest);

}