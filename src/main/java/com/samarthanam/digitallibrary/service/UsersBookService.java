package com.samarthanam.digitallibrary.service;

import com.samarthanam.digitallibrary.constant.BookType;
import com.samarthanam.digitallibrary.dto.response.BookActivityStatus;
import com.samarthanam.digitallibrary.dto.response.BookActivityStatusRequest;
import com.samarthanam.digitallibrary.entity.UserActivityHistory;
import com.samarthanam.digitallibrary.entity.UserBookmarks;
import com.samarthanam.digitallibrary.exception.DuplicateBookmarkRequestException;
import com.samarthanam.digitallibrary.repository.BooksRepository;
import com.samarthanam.digitallibrary.repository.UserActivityHistoryRepository;
import com.samarthanam.digitallibrary.repository.UserBookmarksRepository;
import com.samarthanam.digitallibrary.repository.UserRepository;
import com.samarthanam.digitallibrary.service.mapper.BooksMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
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

    @Autowired
    private AWSCloudService awsCloudService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BooksRepository booksRepository;

    public List<BookActivityStatus> usersBookmarkedBooks(Integer userId, int page, int perPage, BookType bookType) {

        log.info(String.format("Querying bookmarked books for user_id = %d from database", userId));
        var userBookmarks = bookType == null ? userBookmarksRepository.findByUserIdOrderByCreatedTimestampDesc(
                userId, PageRequest.of(page, perPage)) : userBookmarksRepository.findByUserIdAndBookBookTypeFormatBookTypeDescriptionOrderByCreatedTimestampDesc(
                userId, bookType, PageRequest.of(page, perPage));

        return userBookmarks.stream()
                .map((UserBookmarks userBookmark) -> {
                    BookActivityStatus bookActivityStatus = booksMapper.map(userBookmark);
                    bookActivityStatus.getBook().setThumbnailUrl(awsCloudService.generatePresignedUrl(
                            userBookmark.getBook().getThumbnailFileName()));
                    return bookActivityStatus;
                })
                .collect(Collectors.toUnmodifiableList());
    }

    public List<BookActivityStatus> usersRecentlyViewedBooks(Integer userId, int page, int perPage, BookType bookType) {

        log.info(String.format("Querying recently viewed books for user_id = %d from database", userId));
        var userActivityHistory = bookType == null ? userActivityHistoryRepository.findByUserIdOrderByUpdatedTimestamp(
                userId, PageRequest.of(page, perPage)) : userActivityHistoryRepository.findByUserIdAndBookBookTypeFormatBookTypeDescriptionOrderByUpdatedTimestamp(
                userId, bookType, PageRequest.of(page, perPage));

        return userActivityHistory.stream()
                .map((UserActivityHistory userActivityHistoryItem) -> {
                    BookActivityStatus bookActivityStatus = booksMapper.map(userActivityHistoryItem);
                    bookActivityStatus.getBook().setThumbnailUrl(awsCloudService.generatePresignedUrl(
                            userActivityHistoryItem.getBook().getThumbnailFileName()));
                    return bookActivityStatus;
                })
                .collect(Collectors.toUnmodifiableList());
    }

    public void bookmarkBook(BookActivityStatusRequest bookActivityStatusRequest) {

        log.info(String.format("Bookmarking the book isbn = %d for user id = %d",
                               bookActivityStatusRequest.getIsbn(),
                               bookActivityStatusRequest.getUserId()));

        if (!userRepository.existsById(bookActivityStatusRequest.getUserId()))
            throw new ValidationException(String.format("There is no user with user_id = %d", bookActivityStatusRequest.getUserId()));

        if (!booksRepository.existsById(bookActivityStatusRequest.getIsbn()))
            throw new ValidationException(String.format("There is no book with isbn = %d", bookActivityStatusRequest.getIsbn()));

        var userBookmark = booksMapper.mapToUserBookmark(bookActivityStatusRequest);
        if (userBookmarksRepository.existsByUserIdAndBookIsbnAndCurrentPageAndAudioTime(userBookmark.getUserId(),
                                                                                        userBookmark.getBook().getIsbn(),
                                                                                        userBookmark.getCurrentPage(),
                                                                                        userBookmark.getAudioTime()))
            throw new DuplicateBookmarkRequestException("Duplicate bookmark request, requested entry already exists");

        userBookmarksRepository.save(userBookmark);
    }

    public void addBookToRecentlyViewed(BookActivityStatusRequest bookActivityStatusRequest) {

        log.info(String.format("Adding book (isbn = %d) to user's (id = %d) activity history",
                               bookActivityStatusRequest.getIsbn(),
                               bookActivityStatusRequest.getUserId()));

        if (!userRepository.existsById(bookActivityStatusRequest.getUserId()))
            throw new ValidationException(String.format("There is no user with user_id = %d", bookActivityStatusRequest.getUserId()));

        if (!booksRepository.existsById(bookActivityStatusRequest.getIsbn()))
            throw new ValidationException(String.format("There is no book with isbn = %d", bookActivityStatusRequest.getIsbn()));

        var userActivityHistory = booksMapper.mapToUserActivityHistory(bookActivityStatusRequest);
        userActivityHistoryRepository.save(userActivityHistory);
    }

}