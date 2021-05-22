package com.samarthanam.digitallibrary.repository;

import com.samarthanam.digitallibrary.entity.BookTypeFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookTypeFormatsRepository extends JpaRepository<BookTypeFormat, Integer> {

}