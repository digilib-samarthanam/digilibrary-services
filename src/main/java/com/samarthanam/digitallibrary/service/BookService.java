package com.samarthanam.digitallibrary.service;

import com.samarthanam.digitallibrary.entity.Book;
import com.samarthanam.digitallibrary.repository.BooksRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BookService {

    @Autowired
    private BooksRepository booksRepository;

    public List<Book> recentlyAddedBoks(int page, int perPage) {
        return booksRepository.findAllByOrderByCreatedTimestampDesc(PageRequest.of(page, perPage));
    }

}
