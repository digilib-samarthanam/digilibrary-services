package com.samarthanam.digitallibrary.service;

import com.samarthanam.digitallibrary.entity.UserActivityHistory;
import com.samarthanam.digitallibrary.entity.UserBookmarks;
import com.samarthanam.digitallibrary.repository.UserActivityHistoryRepository;
import com.samarthanam.digitallibrary.repository.UserBookmarksRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UsersBookService {

    @Autowired
    private UserBookmarksRepository userBookmarksRepository;

    @Autowired
    private UserActivityHistoryRepository userActivityHistoryRepository;

    public List<UserBookmarks> usersBookmarkedBooks(Integer userId, int page, int perPage) {
        return userBookmarksRepository.findByUserIdOrderByCreatedTimestampDesc(userId, PageRequest.of(page, perPage));
    }

    public List<UserActivityHistory> usersRecentlyViewedBooks(Integer userId, int page, int perPage) {
        return userActivityHistoryRepository.findByUserIdOrderByUpdatedTimestamp(userId, PageRequest.of(page, perPage));
    }

}
