package com.samarthanam.digitallibrary.controller;

import com.samarthanam.digitallibrary.constant.BookType;
import com.samarthanam.digitallibrary.dto.response.Book;
import com.samarthanam.digitallibrary.dto.response.BookActivityStatus;
import com.samarthanam.digitallibrary.dto.response.HomePageResponse;
import com.samarthanam.digitallibrary.service.BookService;
import com.samarthanam.digitallibrary.service.UsersBookService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiOperation("Books Management")
@RestController
@CrossOrigin(origins = {"http://ec2-13-232-236-83.ap-south-1.compute.amazonaws.com:3000", "http://localhost:3000"}, allowedHeaders = "*")
public class BooksController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UsersBookService usersBookService;


    @GetMapping("/recently_added_books")
    public List<Book> recentlyAddedBooks(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage,
            @RequestParam(name = "book_type", required = false) BookType bookType) {

        return bookService.recentlyAddedBooks(page, perPage, bookType);
    }

    @GetMapping("/users/{user_id}/bookmarked_books")
    public List<BookActivityStatus> usersBookmarkedBooks(
            @PathVariable("user_id") Integer userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage,
            @RequestParam(name = "book_type", required = false) BookType bookType) {

        return usersBookService.usersBookmarkedBooks(userId, page, perPage, bookType);
    }

    @GetMapping("/users/{user_id}/recently_viewed_books")
    public List<BookActivityStatus> usersRecentlyViewedBooks(
            @PathVariable("user_id") Integer userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage,
            @RequestParam(name = "book_type", required = false) BookType bookType) {

        return usersBookService.usersRecentlyViewedBooks(userId, page, perPage, bookType);
    }

    @GetMapping("/home_page_books")
    public HomePageResponse getHomePageContent(
            @RequestParam(name = "user_id", required = false) Integer userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage,
            @RequestParam(name = "book_type", required = false) BookType bookType) {

        return HomePageResponse.builder()
                .recentlyViewedBooks(userId == null ? null : usersBookService.usersRecentlyViewedBooks(userId, page, perPage, bookType))
                .bookmarkedBooks(userId == null ? null : usersBookService.usersBookmarkedBooks(userId, page, perPage, bookType))
                .recentlyAddedBooks(bookService.recentlyAddedBooks(page, perPage, bookType))
                .build();
    }

}
