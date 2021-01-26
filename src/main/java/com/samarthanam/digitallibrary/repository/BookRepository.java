package com.samarthanam.digitallibrary.repository;

import com.samarthanam.digitallibrary.entity.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    @EntityGraph("author")
    List<Book> findAllByOrderByCreatedTimestampDesc(Pageable pageRequest);

}