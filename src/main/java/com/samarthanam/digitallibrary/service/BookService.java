package com.samarthanam.digitallibrary.service;

import com.samarthanam.digitallibrary.entity.Book;
import com.samarthanam.digitallibrary.repository.BooksRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookService {

    @Autowired
    private BooksRepository booksRepository;

    public List<Book> recentlyAddedBoks(int page, int perPage) {
//        return booksRepository.findAll(PageRequest.of(page, perPage, Sort.by(Direction.DESC, "createdTimestamp")))
//                .get()
//                .collect(Collectors.toUnmodifiableList());
        return booksRepository.findAllByOrderByCreatedTimestampDesc(PageRequest.of(page, perPage));
    }

}
