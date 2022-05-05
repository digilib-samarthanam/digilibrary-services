package com.samarthanam.digitallibrary.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.samarthanam.digitallibrary.constant.BookType;
import com.samarthanam.digitallibrary.dto.request.BookRequest;
import com.samarthanam.digitallibrary.dto.request.SearchBooksCriteria;
import com.samarthanam.digitallibrary.dto.response.BookResponse;
import com.samarthanam.digitallibrary.dto.response.CategoryResponseDto;
import com.samarthanam.digitallibrary.dto.response.SubCategoryResponseDto;
import com.samarthanam.digitallibrary.entity.*;
import com.samarthanam.digitallibrary.repository.*;
import com.samarthanam.digitallibrary.service.mapper.BooksMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.samarthanam.digitallibrary.constant.ServiceConstants.INDIA_TIME_ZONE;
import static org.apache.poi.ss.usermodel.CellType.STRING;

@Service
@Slf4j
@Validated
public class BookService {
    private static final BooksMapper booksMapper = Mappers.getMapper(BooksMapper.class);

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${s3.bucket.name}")
    private String bucketName;

    @Autowired
    private UserBookmarksRepository userBookmarksRepository;

    @Autowired
    private UserActivityHistoryRepository userActivityHistoryRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private SubCategoriesRepository subCategoriesRepository;

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

        log.info(books.toString());
        return books.stream()
                .map(bookEntity -> {
                    BookResponse book = booksMapper.map(bookEntity);
                    book.setThumbnailUrl(awsCloudService.generatePresignedUrl(
                            bookEntity.getThumbnailFileName()));
                    return book;
                })
                .collect(Collectors.toUnmodifiableList());
    }

    public List<CategoryResponseDto> getBookCategories(int page, int perPage) {
        List<Category> categories = categoriesRepository.findAllByOrderByCategoryName(PageRequest.of(page, perPage));
        log.info(categories.toString());
        return categories.stream()
                .map(categoryEntity -> {
                    CategoryResponseDto category = booksMapper.map(categoryEntity);
                    category.setBooksCount(booksRepository.countByCategoryCategoryId(categoryEntity.getCategoryId()));
                    return category;
                })
                .collect(Collectors.toUnmodifiableList());
    }

    public List<SubCategoryResponseDto> getBookSubCategories(int page, int perPage) {
        List<SubCategory> subCategories = subCategoriesRepository.findAllByOrderBySubCategoryName(PageRequest.of(page, perPage));
        log.info(subCategories.toString());
        return subCategories.stream()
                .map(subCategoryEntity -> {
                    SubCategoryResponseDto subCategory = booksMapper.map(subCategoryEntity);
                    subCategory.setBooksCount(booksRepository.countBySubCategorySubCategoryId(subCategoryEntity.getSubCategoryId()));
                    return subCategory;
                })
                .collect(Collectors.toUnmodifiableList());
    }

    public List<SubCategoryResponseDto> getBookSubCategoriesUnderCategory(int page, int perPage, int categoryId) {
        log.info("Querying books under category_id" + categoryId + "from database");
        List<SubCategory> subCategories = subCategoriesRepository.findByCategoryCategoryIdOrderBySubCategoryName(PageRequest.of(page, perPage),categoryId);

        log.info(subCategories.toString());
        return subCategories.stream()
                .map(subCategoryEntity -> {
                    SubCategoryResponseDto subCategory = booksMapper.map(subCategoryEntity);
                    subCategory.setBooksCount(booksRepository.countBySubCategorySubCategoryId(subCategoryEntity.getSubCategoryId()));
                    return subCategory;
                })
                .collect(Collectors.toUnmodifiableList());
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

        var subCategory = subCategoriesRepository.findFirstBySubCategoryNameIgnoreCaseAndCategoryCategoryId(bookRequest.getSubCategoryName(),category.getCategoryId())
                .orElseGet(() -> subCategoriesRepository.save(SubCategory.builder()
                        .subCategoryName(bookRequest.getSubCategoryName())
                        .category(category)
                        .createdTimestamp(LocalDateTime.now(INDIA_TIME_ZONE))
                        .build()));
        book.setSubCategory(subCategory);

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

    public List<BookResponse> getAllBooksBySubCategory(int sub_category_id, int page, int perPage) {

        log.info("Querying books under sub_category_id" + sub_category_id + "from database");
        var books = booksRepository.findBySubCategorySubCategoryIdOrderByCreatedTimestampDesc(sub_category_id, PageRequest.of(page, perPage));

        log.info(books.toString());
        return books.stream()
                .map(bookEntity -> {
                    BookResponse book = booksMapper.map(bookEntity);
                    book.setThumbnailUrl(awsCloudService.generatePresignedUrl(
                            bookEntity.getThumbnailFileName()));
                    return book;
                })
                .collect(Collectors.toUnmodifiableList());
    }

    public void ReadDataFromExcel(String fileName) {

        try {
            S3Object object = amazonS3.getObject(new GetObjectRequest(bucketName, "bulk_upload_books/"+fileName));
            InputStream objectData = object.getObjectContent();

            XSSFWorkbook workbook = new XSSFWorkbook(objectData);
            ExecutorService asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<Future<?>> futures = new ArrayList<Future<?>>();
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Future<?> f = asyncExecutor.submit(() -> {
                                process(row);
                            }
                    );
                    futures.add(f);

            }

            for(Future<?> future : futures)
                future.get();
            boolean allDone = true;
            for(Future<?> future : futures){
                allDone &= future.isDone(); // check if future is done
            }
            if(allDone){
                File outputFile = new File(fileName);
                FileOutputStream fos = new FileOutputStream(outputFile);
                workbook.write(fos);
                try{
                    final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, "bulk_upload_books_status/upload_status_"+fileName, outputFile);
                    amazonS3.putObject(putObjectRequest);
                    outputFile.delete();
                }catch(Exception e){
                    log.info("error uploading status file to s3");
                }
                workbook.close();
            }

        } catch (IOException e) {
            log.error("Error reading file");
            e.printStackTrace();
        } catch (AmazonS3Exception e){
            log.error("Bulk Upload file not found in S3");
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void process(Row row){

        try{
            String authorName = row.getCell(0).getStringCellValue();
            Integer isbn = ((int) row.getCell(1).getNumericCellValue());
            String categoryName = row.getCell(2).getStringCellValue();
            String subCategory = row.getCell(3).getStringCellValue();
            String bookType = row.getCell(4).getStringCellValue();
            String title = row.getCell(5).getStringCellValue();
            String year = String.valueOf((int) row.getCell(6).getNumericCellValue());
            String countryOfOrigin = row.getCell(7).getStringCellValue();
            String editionVersion = String.valueOf(row.getCell(8).getNumericCellValue());
            Integer totalPages = (int) row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue();
            Date totalAudioTime = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getDateCellValue();
            SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
            String audioTime;
            if (totalAudioTime == null) {
                audioTime = null;
            } else audioTime = formatTime.format(totalAudioTime);
            String fileName = row.getCell(11).getStringCellValue();
            if(isFilePresent(fileName,bookType)) {
                BookRequest bookRequest = new BookRequest();
                bookRequest.setAuthorName(authorName);
                bookRequest.setIsbn(isbn);
                bookRequest.setCategoryName(categoryName);
                bookRequest.setSubCategoryName(subCategory);
                bookRequest.setBookType(BookType.valueOf(bookType));
                bookRequest.setTitle(title);
                bookRequest.setYear(year);
                bookRequest.setCountryOfOrigin(countryOfOrigin);
                bookRequest.setEditionVersion(editionVersion);
                if (totalPages == 0) {
                    bookRequest.setTotalPages(null);
                } else {
                    bookRequest.setTotalPages(totalPages);
                }
                bookRequest.setTotalAudioTime(audioTime);

                bookRequest.setFileName(fileName);
                createBook(bookRequest);
                row.createCell(12, STRING);
                row.getCell(12).setCellValue("Success");
            }else throw new FileNotFoundException("The file does not exist in S3 bucket");
        }catch(Exception e){
            log.info(e.getMessage());
            row.createCell(12, STRING);
            row.getCell(12).setCellValue("Failure");
            row.createCell(13, STRING);
            row.getCell(13).setCellValue(e.getMessage());
        }

    }

    public boolean isFilePresent(String fileName,String bookType){
        String prefix = "";
        if(bookType.equals("PDF"))
            fileName += ".pdf";
        else
            fileName += ".mp3";
        List<String> files = awsCloudService.getFiles(bucketName,prefix);
        fileName = prefix+fileName;
        return files.contains(fileName);
    }

}
