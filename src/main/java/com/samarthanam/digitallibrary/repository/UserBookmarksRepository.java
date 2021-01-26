package com.samarthanam.digitallibrary.repository;

import com.samarthanam.digitallibrary.entity.UserBookmarks;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBookmarksRepository extends JpaRepository<UserBookmarks, Integer> {

    List<UserBookmarks> findByUserIdOrderByCreatedTimestampDesc(Integer userId, Pageable pageRequest);

}