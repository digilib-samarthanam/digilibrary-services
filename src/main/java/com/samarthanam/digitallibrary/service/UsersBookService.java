package com.samarthanam.digitallibrary.service;

import com.samarthanam.digitallibrary.dto.response.Book;
import com.samarthanam.digitallibrary.dto.response.BookActivityStatus;
import com.samarthanam.digitallibrary.entity.UserBookmarks;
import com.samarthanam.digitallibrary.repository.UserActivityHistoryRepository;
import com.samarthanam.digitallibrary.repository.UserBookmarksRepository;
import com.samarthanam.digitallibrary.service.mapper.BooksMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UsersBookService {

    private static final BooksMapper booksMapper = Mappers.getMapper(BooksMapper.class);

    @Autowired
    private UserBookmarksRepository userBookmarksRepository;

    @Autowired
    private UserActivityHistoryRepository userActivityHistoryRepository;

    public List<Book> usersBookmarkedBooks(Integer userId, int page, int perPage) {
        log.info(String.format("Querying bookmarked books for user_id = %d from database", userId));
        var userBookmarks = userBookmarksRepository.findByUserIdOrderByCreatedTimestampDesc(userId, PageRequest.of(page, perPage));
        return userBookmarks.stream()
                .map(UserBookmarks::getBook)
                .map(booksMapper::mapToBook)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<BookActivityStatus> usersRecentlyViewedBooks(Integer userId, int page, int perPage) {
        log.info(String.format("Querying recently viewed books for user_id = %d from database", userId));
        var userActivityHistory = userActivityHistoryRepository.findByUserIdOrderByUpdatedTimestamp(userId, PageRequest.of(page, perPage));
        return booksMapper.mapToBookActivityStatuses(userActivityHistory);
    }

}
