package com.samarthanam.digitallibrary.service;

import com.samarthanam.digitallibrary.dto.response.Book;
import com.samarthanam.digitallibrary.repository.BooksRepository;
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
public class BookService {

    private static final BooksMapper booksMapper = Mappers.getMapper(BooksMapper.class);

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private AWSCloudService awsCloudService;

    public List<Book> recentlyAddedBooks(int page, int perPage) {
        log.info("Querying recently added books from database");
        var books = booksRepository.findAllByOrderByCreatedTimestampDesc(PageRequest.of(page, perPage));
        return books.stream()
                .map(bookEntity -> {
                    Book book = booksMapper.map(bookEntity);
                    book.setThumbnailUrl(awsCloudService.generatePresignedUrl(
                            bookEntity.getThumbnailFileName()));
                    return book;
                })
                .collect(Collectors.toUnmodifiableList());
    }

}
