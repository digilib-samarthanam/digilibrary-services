package com.samarthanam.digitallibrary.controller;

import com.samarthanam.digitallibrary.dto.response.HomePageResponse;
import com.samarthanam.digitallibrary.entity.Book;
import com.samarthanam.digitallibrary.entity.UserActivityHistory;
import com.samarthanam.digitallibrary.entity.UserBookmarks;
import com.samarthanam.digitallibrary.service.BookService;
import com.samarthanam.digitallibrary.service.UsersBookService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiOperation("Books Management")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BooksController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UsersBookService usersBookService;


    @GetMapping("/recently_added_books")
    public List<Book> recentlyAddedBooks(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage) {

        return bookService.recentlyAddedBooks(page, perPage);
    }

    @GetMapping("/users/{user_id}/bookmarked_books")
    public List<UserBookmarks> usersBookmarkedBooks(
            @PathVariable("user_id") Integer userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage) {

        return usersBookService.usersBookmarkedBooks(userId, page, perPage);
    }

    @GetMapping("/users/{user_id}/recently_viewed_books")
    public List<UserActivityHistory> usersRecentlyViewedBooks(
            @PathVariable("user_id") Integer userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage) {

        return usersBookService.usersRecentlyViewedBooks(userId, page, perPage);
    }

    @GetMapping("/books")
    public HomePageResponse getHomePageContent(
            @RequestParam(name = "user_id", required = false) Integer userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage) {

        return HomePageResponse.builder()
                .recentlyViewedBooks(userId == null ? null : usersBookService.usersRecentlyViewedBooks(userId, page, perPage))
                .bookmarkedBooks(userId == null ? null : usersBookService.usersBookmarkedBooks(userId, page, perPage))
                .recentlyAddedBooks(bookService.recentlyAddedBooks(page, perPage))
                .build();
    }

}
