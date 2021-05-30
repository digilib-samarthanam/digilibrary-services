package com.samarthanam.digitallibrary.repository;

import com.samarthanam.digitallibrary.entity.Author;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorsRepository extends JpaRepository<Author, Integer> {

    List<Author> findAllByOrderByName(Pageable pageRequest);
    Optional<Author> findFirstByNameIgnoreCase(String name);

}