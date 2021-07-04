package com.samarthanam.digitallibrary.repository;

import com.samarthanam.digitallibrary.constant.BookType;
import com.samarthanam.digitallibrary.entity.UserActivityHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityHistoryRepository extends JpaRepository<UserActivityHistory, Integer> {

    @EntityGraph(attributePaths = { "book.author", "book.category", "book.bookTypeFormat" })
    List<UserActivityHistory> findByUserIdOrderByUpdatedTimestamp(Integer userId, Pageable pageRequest);

    @EntityGraph(attributePaths = { "book.author", "book.category", "book.bookTypeFormat" })
    List<UserActivityHistory> findByUserIdAndBookBookTypeFormatBookTypeDescriptionOrderByUpdatedTimestamp(Integer userId, BookType bookType, Pageable pageRequest);

    void deleteByBookIsbn(Integer isbn);

}