package com.samarthanam.digitallibrary.controller;

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
    public List<Book> recentlyAddedBoks(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage) {

        return bookService.recentlyAddedBoks(page, perPage);
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

}
