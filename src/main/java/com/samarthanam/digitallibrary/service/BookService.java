package com.samarthanam.digitallibrary.service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.samarthanam.digitallibrary.constant.BookType;
import com.samarthanam.digitallibrary.dto.request.BookRequest;
import com.samarthanam.digitallibrary.dto.request.SearchBooksCriteria;
import com.samarthanam.digitallibrary.dto.response.BookResponse;
import com.samarthanam.digitallibrary.entity.Author;
import com.samarthanam.digitallibrary.entity.Book;
import com.samarthanam.digitallibrary.entity.BookTypeFormat;
import com.samarthanam.digitallibrary.entity.Category;
import com.samarthanam.digitallibrary.repository.*;
import com.samarthanam.digitallibrary.service.mapper.BooksMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE;

@Service
@Slf4j
@Validated
public class BookService {
    private static final BooksMapper booksMapper = Mappers.getMapper(BooksMapper.class);

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private UserBookmarksRepository userBookmarksRepository;

    @Autowired
    private UserActivityHistoryRepository userActivityHistoryRepository;

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

    public void createBook(@Valid BookRequest bookRequest) {

        log.info("Inserting a new book in database; ISBN : " + bookRequest.getIsbn());
        if (booksRepository.existsById(bookRequest.getIsbn()))
            throw new ValidationException(String.format("There is already an book present with isbn = %d", bookRequest.getIsbn()));

        saveBook(bookRequest, LocalDateTime.now(INDIA_TIME_ZONE));
    }

    private void saveBook(@Valid BookRequest bookRequest, LocalDateTime createdTimestamp) {

        switch (bookRequest.getBookType()) {
            case PDF:
                if (bookRequest.getTotalAudioTime() != null)
                    throw new ValidationException("PDF Books can not have total_audio_time");
                else if (bookRequest.getTotalPages() == null)
                    throw new ValidationException("PDF Books must have total_pages");
                break;

            case AUDIO_BOOK:
                if (bookRequest.getTotalPages() != null)
                    throw new ValidationException("Audio Books can not have total_pages");
                else if (bookRequest.getTotalAudioTime() == null)
                    throw new ValidationException("Audio Books must have total_audio_time");
        }

        var book = booksMapper.map(bookRequest);

        var author = authorsRepository.findFirstByNameIgnoreCase(bookRequest.getAuthorName())
                .orElseGet(() -> authorsRepository.save(Author.builder()
                                                                .name(bookRequest.getAuthorName())
                                                                .createdTimestamp(LocalDateTime.now(INDIA_TIME_ZONE))
                                                                .build()));
        book.setAuthor(author);

        var category = categoriesRepository.findFirstByCategoryNameIgnoreCase(bookRequest.getCategoryName())
                .orElseGet(() -> categoriesRepository.save(Category.builder()
                                                                .categoryName(bookRequest.getCategoryName())
                                                                .createdTimestamp(LocalDateTime.now(INDIA_TIME_ZONE))
                                                                .build()));
        book.setCategory(category);

        book.setCreatedTimestamp(createdTimestamp);
        book.setBookTypeFormat(bookTypes.get(bookRequest.getBookType()));
        booksRepository.save(book);
    }

    public void updateBook(@Valid BookRequest bookRequest) {

        log.info("Updating the book details in database for ISBN : " + bookRequest.getIsbn());
        var existingBook = booksRepository.findById(bookRequest.getIsbn())
                .orElseThrow(() -> new ValidationException(String.format("We couldn't find any book with isbn = %d", bookRequest.getIsbn())));

        saveBook(bookRequest, existingBook.getCreatedTimestamp());
    }

    @Transactional
    public void deleteBook(@PathVariable @Positive Integer isbn) {

        log.info("Deleting the book with ISBN : " + isbn + " in database");
        if (!booksRepository.existsById(isbn))
            throw new ValidationException(String.format("We couldn't find any book with isbn = %d", isbn));

        userBookmarksRepository.deleteByBookIsbn(isbn);
        userActivityHistoryRepository.deleteByBookIsbn(isbn);
        booksRepository.deleteById(isbn);
    }

    public void ReadDataFromExcel(){

        try {
//            FileInputStream file = new FileInputStream(new File(pathname));
//            AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
//            S3ObjectSummary objectListing = amazonS3.listObjects("samarthanam-personal-development","bulk_upload_books/DigitalLibraryExcel.xlsx").;

            S3Object object = amazonS3.getObject(new GetObjectRequest("samarthanam-personal-development","bulk_upload_books/DigitalLibraryExcel.xlsx"));
            InputStream objectData = object.getObjectContent();
            XSSFWorkbook workbook = new XSSFWorkbook(objectData);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String authorName = row.getCell(0).getStringCellValue();
                Integer isbn = ((int) row.getCell(1).getNumericCellValue());
                String categoryName=row.getCell(2).getStringCellValue();
                String bookType= row.getCell(3).getStringCellValue();
                String title = row.getCell(4).getStringCellValue();
                String year = String.valueOf((int)row.getCell(5).getNumericCellValue());
                String countryOfOrigin = row.getCell(6).getStringCellValue();
                String editionVersion =String.valueOf(row.getCell(7).getNumericCellValue());
                Integer totalPages=(int)row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue();
                Date totalAudioTime = row.getCell(9).getDateCellValue();
                SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
                String audioTime;
                if(totalAudioTime==null){
                    audioTime=null;
                }
                else audioTime =formatTime.format(totalAudioTime);
                String fileName = row.getCell(10).getStringCellValue();

                BookRequest bookRequest = new BookRequest();
                bookRequest.setAuthorName(authorName);
                bookRequest.setIsbn(isbn);
                bookRequest.setCategoryName(categoryName);
                bookRequest.setBookType(BookType.valueOf(bookType));
                bookRequest.setTitle(title);
                bookRequest.setYear(year);
                bookRequest.setCountryOfOrigin(countryOfOrigin);
                bookRequest.setEditionVersion(editionVersion);
                if(totalPages==0){
                    bookRequest.setTotalPages(null);
                }
                else{
                bookRequest.setTotalPages(totalPages);
                }
                bookRequest.setTotalAudioTime(audioTime);

                bookRequest.setFileName(fileName);

                createBook(bookRequest);
            }
            workbook.close();
        } catch (IOException e) {
            log.error("Error reading file");
            e.printStackTrace();
        }
    }

}
