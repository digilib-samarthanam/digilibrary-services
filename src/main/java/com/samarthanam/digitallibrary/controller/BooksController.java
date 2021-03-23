package com.samarthanam.digitallibrary.controller;

import com.samarthanam.digitallibrary.dto.request.SearchBooksCriteria;
import com.samarthanam.digitallibrary.dto.response.BookResponse;
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
    public List<BookResponse> recentlyAddedBooks(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage) {

        return bookService.recentlyAddedBooks(page, perPage);
    }

    @GetMapping("/users/{user_id}/bookmarked_books")
    public List<BookResponse> usersBookmarkedBooks(
            @PathVariable("user_id") Integer userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage) {

        return usersBookService.usersBookmarkedBooks(userId, page, perPage);
    }

    @GetMapping("/users/{user_id}/recently_viewed_books")
    public List<BookActivityStatus> usersRecentlyViewedBooks(
            @PathVariable("user_id") Integer userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage) {

        return usersBookService.usersRecentlyViewedBooks(userId, page, perPage);
    }

    @GetMapping("/home_page_books")
    public HomePageResponse getHomePageContent(
            @RequestParam(name = "user_id", required = false) Integer userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage) {

        return HomePageResponse.builder()
                .recentlyViewedBooks(userId == null ? null : usersBookService.usersRecentlyViewedBooks(userId, page, perPage))
                .bookmarkedBookResponses(userId == null ? null : usersBookService.usersBookmarkedBooks(userId, page, perPage))
                .recentlyAddedBookResponses(bookService.recentlyAddedBooks(page, perPage))
                .build();
    }

    @GetMapping("/search")
    public List<BookResponse> searchBooks(
            @RequestParam(name = "any_book", required = false) String anyBook,
            @RequestParam(name = "book_name", required = false) String bookName,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "bookType", required = false) String bookType,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage) {


        SearchBooksCriteria searchBooksCriteria = new SearchBooksCriteria(anyBook, bookName , category ,author , bookType ) ;
        return bookService.searchBooks(searchBooksCriteria, page, perPage);
    }

}
