package com.samarthanam.digitallibrary.repository;

import com.samarthanam.digitallibrary.constant.BookType;
import com.samarthanam.digitallibrary.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {

    @EntityGraph(attributePaths = {"author", "category", "bookTypeFormat","subCategory"})
    List<Book> findByOrderByCreatedTimestampDesc(Pageable pageRequest);

    @EntityGraph(attributePaths = {"author", "category", "bookTypeFormat", "subCategory"})
    List<Book> findByBookTypeFormatBookTypeDescriptionOrderByCreatedTimestampDesc(BookType bookType, Pageable pageRequest);

}