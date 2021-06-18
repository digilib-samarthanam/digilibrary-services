package com.samarthanam.digitallibrary.service;

import com.samarthanam.digitallibrary.constant.BookType;
import com.samarthanam.digitallibrary.dto.request.BookCreateRequest;
import com.samarthanam.digitallibrary.dto.request.SearchBooksCriteria;
import com.samarthanam.digitallibrary.dto.response.BookResponse;
import com.samarthanam.digitallibrary.entity.Author;
import com.samarthanam.digitallibrary.entity.Book;
import com.samarthanam.digitallibrary.entity.BookTypeFormat;
import com.samarthanam.digitallibrary.entity.Category;
import com.samarthanam.digitallibrary.repository.AuthorsRepository;
import com.samarthanam.digitallibrary.repository.BookTypeFormatsRepository;
import com.samarthanam.digitallibrary.repository.BooksRepository;
import com.samarthanam.digitallibrary.repository.CategoriesRepository;
import com.samarthanam.digitallibrary.service.mapper.BooksMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE;

@Service
@Slf4j
@Validated
public class BookService {
    private static final BooksMapper booksMapper = Mappers.getMapper(BooksMapper.class);

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private AuthorsRepository authorsRepository;

    @Autowired
    private BookTypeFormatsRepository bookTypeFormatsRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AWSCloudService awsCloudService;

    private EnumMap<BookType, BookTypeFormat> bookTypes;

    @PostConstruct
    private void cacheBookTypes() {

        log.info("Caching Book Types on boot up");
        bookTypes = bookTypeFormatsRepository.findAll()
                .stream()
                .collect(Collectors.toMap(BookTypeFormat::getBookTypeDescription,
                                          Function.identity(),
                                          (l, r) -> {
                                              throw new IllegalArgumentException("Duplicate keys " + l + "and " + r + ".");
                                          },
                                          () -> new EnumMap<>(BookType.class)));
    }

    public List<BookResponse> recentlyAddedBooks(int page, int perPage, BookType bookType) {

        log.info("Querying recently added books from database");
        var books = bookType == null ? booksRepository.findByOrderByCreatedTimestampDesc(PageRequest.of(page, perPage))
                : booksRepository.findByBookTypeFormatBookTypeDescriptionOrderByCreatedTimestampDesc(bookType, PageRequest.of(page, perPage));

        return books.stream()
                .map(bookEntity -> {
                    BookResponse book = booksMapper.map(bookEntity);
                    book.setThumbnailUrl(awsCloudService.generatePresignedUrl(
                            bookEntity.getThumbnailFileName()));
                    return book;
                })
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Category> getBookCategories(int page, int perPage) {
        return categoriesRepository.findAllByOrderByCategoryName(PageRequest.of(page, perPage));
    }

    public List<Author> getAuthors(int page, int perPage) {
        return authorsRepository.findAllByOrderByName(PageRequest.of(page, perPage));
    }

    public List<BookResponse> searchBooks(SearchBooksCriteria searchBooksCriteria, int page, int perPage) {

        log.info("Querying book details from database");
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
        Root<Book> classData = criteriaQuery.from(Book.class);

        if (searchBooksCriteria.getAnyBook() != null && !searchBooksCriteria.getAnyBook().equalsIgnoreCase("")) {

            Predicate predicateForTitle
                    = criteriaBuilder.like(criteriaBuilder.lower(classData.get("title")), "%" + searchBooksCriteria.getAnyBook().toLowerCase() + "%");
            Predicate predicateForAuthorName
                    = criteriaBuilder.like(criteriaBuilder.lower(classData.get("author").get("name")), "%" + searchBooksCriteria.getAnyBook().toLowerCase() + "%");
            Predicate predicateForDescription
                    = criteriaBuilder.like(criteriaBuilder.lower(classData.get("bookTypeFormat").get("bookTypeDescription").as(String.class)), "%" + searchBooksCriteria.getAnyBook().toLowerCase() + "%");
            Predicate finalPredicate
                    = criteriaBuilder.or(predicateForAuthorName, predicateForTitle, predicateForDescription);
            criteriaQuery.select(classData).where(finalPredicate);
        } else {
            List<Predicate> predicates = buildPredicates(searchBooksCriteria, criteriaBuilder, classData);
            criteriaQuery.select(classData).where(criteriaBuilder.and(predicates.toArray(new Predicate[]{})));
        }
        var books = entityManager.createQuery(criteriaQuery)
                .setFirstResult(page * perPage)
                .setMaxResults(perPage)
                .getResultList();

        return books.stream()
                .map(bookEntity -> {
                    BookResponse book = booksMapper.map(bookEntity);
                    book.setThumbnailUrl(awsCloudService.generatePresignedUrl(
                            bookEntity.getThumbnailFileName()));
                    return book;
                })
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Predicate> buildPredicates(SearchBooksCriteria searchBooksCriteria, CriteriaBuilder criteriaBuilder, Root<Book> classData) {

        List<Predicate> predicateList = new ArrayList<>();

        if (searchBooksCriteria.getAuthor() != null && !searchBooksCriteria.getAuthor().equalsIgnoreCase("")) {
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(classData.get("author").get("name")),
                                                   "%" + searchBooksCriteria.getAuthor().toLowerCase() + "%"));
        }
        if (searchBooksCriteria.getBookName() != null && !searchBooksCriteria.getBookName().equalsIgnoreCase("")) {
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(classData.get("title")), "%" + searchBooksCriteria.getBookName().toLowerCase() + "%"));
        }
        if (searchBooksCriteria.getBookType() != null && !searchBooksCriteria.getBookType().equalsIgnoreCase("")) {
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(classData.get("bookTypeFormat").get("bookTypeDescription").as(String.class)), "%" + searchBooksCriteria.getBookType().toLowerCase() + "%"));
        }
        if (searchBooksCriteria.getCategory() != null && !searchBooksCriteria.getCategory().equalsIgnoreCase("")) {
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(classData.get("category").get("categoryName")), "%" + searchBooksCriteria.getCategory().toLowerCase() + "%"));
        }

        return predicateList;
    }

    public void createBook(@Valid BookCreateRequest bookCreateRequest) {

        log.info("Inserting a new book in database; ISBN : " + bookCreateRequest.getIsbn());
        if (booksRepository.existsById(bookCreateRequest.getIsbn()))
            throw new ValidationException(String.format("There is already an book present with isbn = %d", bookCreateRequest.getIsbn()));

        saveBook(bookCreateRequest);
    }

    private void saveBook(@Valid BookCreateRequest bookCreateRequest) {

        if (!categoriesRepository.existsById(bookCreateRequest.getCategoryId()))
            throw new ValidationException(String.format("There is no category with category_id = %d", bookCreateRequest.getCategoryId()));

        if (bookCreateRequest.getAuthorId() != null) {

            if (bookCreateRequest.getAuthorName() != null)
                throw new ValidationException("Please provide only one attribute out of [ author_id, author_name ]");

            if (!authorsRepository.existsById(bookCreateRequest.getAuthorId()))
                throw new ValidationException(String.format("There is no author with author_id = %d", bookCreateRequest.getAuthorId()));

        } else if (bookCreateRequest.getAuthorName() == null)
            throw new ValidationException("Both attributes author_id and author_name are missing");

        else {
            var author = authorsRepository.findFirstByNameIgnoreCase(bookCreateRequest.getAuthorName())
                    .orElseGet(() -> authorsRepository.save(Author.builder()
                                                                    .name(bookCreateRequest.getAuthorName())
                                                                    .createdTimestamp(LocalDateTime.now(INDIA_TIME_ZONE))
                                                                    .build()));

            bookCreateRequest.setAuthorId(author.getAuthorId());
        }

        switch (bookCreateRequest.getBookType()) {
            case PDF:
                if (bookCreateRequest.getTotalAudioTime() != null)
                    throw new ValidationException("PDF Books can not have total_audio_time");
                else if (bookCreateRequest.getTotalPages() == null)
                    throw new ValidationException("PDF Books must have total_pages");
                break;

            case AUDIO_BOOK:
                if (bookCreateRequest.getTotalPages() != null)
                    throw new ValidationException("Audio Books can not have total_pages");
                else if (bookCreateRequest.getTotalAudioTime() == null)
                    throw new ValidationException("Audio Books must have total_audio_time");
        }

        var book = booksMapper.map(bookCreateRequest);
        book.setBookTypeFormat(bookTypes.get(bookCreateRequest.getBookType()));

        booksRepository.save(book);
    }

    public void updateBook(@Valid BookCreateRequest bookCreateRequest) {

        log.info("Updating the book details in database for ISBN : " + bookCreateRequest.getIsbn());
        if (!booksRepository.existsById(bookCreateRequest.getIsbn()))
            throw new ValidationException(String.format("We couldn't find any book with isbn = %d", bookCreateRequest.getIsbn()));

        saveBook(bookCreateRequest);
    }

    public void disableBook(@PathVariable @Positive Integer isbn) {

        log.info("Disabling the book with ISBN : " + bookCreateRequest.getIsbn() + " in database");
        var book = booksRepository.findById(bookCreateRequest.getIsbn())
                .orElseThrow(() -> new ValidationException(String.format("We couldn't find any book with isbn = %d", isbn())));

        book.setActive(false);
        booksRepository.save(book);
    }

}
