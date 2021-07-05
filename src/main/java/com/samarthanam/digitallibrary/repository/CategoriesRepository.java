package com.samarthanam.digitallibrary.repository;

import com.samarthanam.digitallibrary.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<Category, Integer> {

    List<Category> findAllByOrderByCategoryName(Pageable pageRequest);
    Optional<Category> findFirstByCategoryNameIgnoreCase(String CategoryName);

}