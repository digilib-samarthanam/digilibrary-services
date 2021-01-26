package com.samarthanam.digitallibrary.repository;

import com.samarthanam.digitallibrary.entity.UserActivityHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityHistoryRepository extends JpaRepository<UserActivityHistory, Integer> {

    List<UserActivityHistory> findByUserIdOrderByUpdatedTimestamp(Integer userId, Pageable pageRequest);

}