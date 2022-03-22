package com.samarthanam.digitallibrary.controller;

import com.samarthanam.digitallibrary.constant.BookType;
import com.samarthanam.digitallibrary.dto.request.BookRequest;
import com.samarthanam.digitallibrary.dto.request.SearchBooksCriteria;
import com.samarthanam.digitallibrary.dto.response.BookActivityStatus;
import com.samarthanam.digitallibrary.dto.response.BookActivityStatusRequest;
import com.samarthanam.digitallibrary.dto.response.BookResponse;
import com.samarthanam.digitallibrary.dto.response.HomePageResponse;
import com.samarthanam.digitallibrary.entity.Author;
import com.samarthanam.digitallibrary.entity.Category;
import com.samarthanam.digitallibrary.entity.SubCategory;
import com.samarthanam.digitallibrary.service.BookService;
import com.samarthanam.digitallibrary.service.UsersBookService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
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
    public List<BookResponse> recentlyAddedBooks(
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

    @GetMapping("/search")
    public List<BookResponse> searchBooks(
            @RequestParam(name = "any_book", required = false) String anyBook,
            @RequestParam(name = "book_name", required = false) String bookName,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "bookType", required = false) String bookType,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "10") int perPage) {


        SearchBooksCriteria searchBooksCriteria = new SearchBooksCriteria(anyBook, bookName, category, author, bookType);
        return bookService.searchBooks(searchBooksCriteria, page, perPage);
    }


    @PostMapping("/bookmarked_books")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void bookmarkBook(@RequestBody BookActivityStatusRequest bookActivityStatusRequest) {

        usersBookService.bookmarkBook(bookActivityStatusRequest);
    }

    @PostMapping("/recently_viewed_books")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addBookToRecentlyViewed(@RequestBody BookActivityStatusRequest bookActivityStatusRequest) {

        usersBookService.addBookToRecentlyViewed(bookActivityStatusRequest);
    }

    @GetMapping("/book_categories")
    public List<Category> getBookCategories(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "2147483647") int perPage) {

        return bookService.getBookCategories(page, perPage);
    }

    @GetMapping("/book_sub_categories")
    public List<SubCategory> getBookSubCategories(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "2147483647") int perPage) {

        return bookService.getBookSubCategories(page, perPage);
    }

    @GetMapping("/book_sub_categories/{categoryId}")
    public List<SubCategory> getBookSubCategoriesByCategoryId(
            @PathVariable @Positive Integer categoryId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "2147483647") int perPage) {

        return bookService.getBookSubCategoriesUnderCategory(page, perPage,categoryId);
    }

    @GetMapping("/authors")
    public List<Author> getAuthors(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "per_page", required = false, defaultValue = "2147483647") int perPage) {

        return bookService.getAuthors(page, perPage);
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createBook(@RequestBody @Valid BookRequest bookRequest) {

        bookService.createBook(bookRequest);
    }

    @PutMapping("/books/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createBook(@PathVariable @Positive Integer isbn,
            @RequestBody BookRequest bookRequest) {

        bookRequest.setIsbn(isbn);
        bookService.updateBook(bookRequest);
    }

    @DeleteMapping("/books/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable @Positive Integer isbn) {

        bookService.deleteBook(isbn);
    }

    @GetMapping("/upload")
    public void BulkUpload(){
        bookService.ReadDataFromExcel();
    }
}
