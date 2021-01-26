package com.samarthanam.digitallibrary.repository;

import com.samarthanam.digitallibrary.entity.UserActivityHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface UserActivityHistoryRepository extends JpaRepository<UserActivityHistory, Integer> {

    List<UserActivityHistory> findByUserIdOrderByUpdateTimestamp(Integer userId, Pageable pageRequest);

}