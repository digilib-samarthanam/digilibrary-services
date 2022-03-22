package com.samarthanam.digitallibrary.repository;


import com.samarthanam.digitallibrary.entity.SubCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoriesRepository extends JpaRepository<SubCategory, Integer> {

    @EntityGraph(attributePaths = { "category.categoryId"})
    List<SubCategory> findAllByOrderBySubCategoryName(Pageable pageRequest);
    Optional<SubCategory> findFirstBySubCategoryNameIgnoreCase(String subCategoryName);

    @EntityGraph(attributePaths = { "category.categoryId"})
    List<SubCategory> findByCategoryCategoryIdOrderBySubCategoryName(Pageable pageRequest, Integer categoryId);

}