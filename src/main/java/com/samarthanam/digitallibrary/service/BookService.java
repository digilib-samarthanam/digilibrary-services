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
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
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
            Predicate predicateForAuthorFirstName
                    = criteriaBuilder.like(criteriaBuilder.lower(classData.get("author").get("firstName")), "%" + searchBooksCriteria.getAnyBook().toLowerCase() + "%");
            Predicate predicateForAuthorLastName
                    = criteriaBuilder.like(criteriaBuilder.lower(classData.get("author").get("lastName")), "%" + searchBooksCriteria.getAnyBook().toLowerCase() + "%");
            Predicate predicateForDescription
                    = criteriaBuilder.like(criteriaBuilder.lower(classData.get("bookTypeFormat").get("bookTypeDescription").as(String.class)), "%" + searchBooksCriteria.getAnyBook().toLowerCase() + "%");
            Predicate finalPredicate
                    = criteriaBuilder.or(predicateForAuthorFirstName, predicateForAuthorLastName, predicateForTitle, predicateForDescription);
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
            predicateList.add(criteriaBuilder.or(criteriaBuilder
                                                         .like(criteriaBuilder.lower(classData.get("author").get("firstName")),
                                                               "%" + searchBooksCriteria.getAuthor().toLowerCase() + "%"),
                                                 criteriaBuilder
                                                         .like(criteriaBuilder.lower(classData.get("author").get("lastName")),
                                                               "%" + searchBooksCriteria.getAuthor().toLowerCase() + "%")
            ));
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

    public void createBook(@RequestBody BookCreateRequest bookCreateRequest) {

        if (!booksRepository.existsById(bookCreateRequest.getIsbn()))
            throw new ValidationException(String.format("There is already an book present with isbn = %d", bookCreateRequest.getIsbn()));

        if (!authorsRepository.existsById(bookCreateRequest.getAuthorId()))
            throw new ValidationException(String.format("There is no author with author_id = %d", bookCreateRequest.getAuthorId()));

        if (!categoriesRepository.existsById(bookCreateRequest.getCategoryId()))
            throw new ValidationException(String.format("There is no category with category_id = %d", bookCreateRequest.getCategoryId()));

        var book = booksMapper.map(bookCreateRequest);
        book.setBookTypeFormat(bookTypes.get(bookCreateRequest.getBookType()));

        booksRepository.save(book);
    }

}
