package com.samarthanam.digitallibrary.service;

import com.samarthanam.digitallibrary.dto.request.SearchBooksCriteria;
import com.samarthanam.digitallibrary.dto.response.BookResponse;
import com.samarthanam.digitallibrary.entity.Author;
import com.samarthanam.digitallibrary.entity.Book;
import com.samarthanam.digitallibrary.repository.BooksRepository;
import com.samarthanam.digitallibrary.service.mapper.BooksMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BookService {

    private static final BooksMapper booksMapper = Mappers.getMapper(BooksMapper.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BooksRepository booksRepository;

    public List<BookResponse> recentlyAddedBooks(int page, int perPage) {
        log.info("Querying recently added books from database");
        var books = booksRepository.findAllByOrderByCreatedTimestampDesc(PageRequest.of(page, perPage));
        return booksMapper.mapToBooks(books);
    }

    public List<BookResponse> searchBooks(SearchBooksCriteria searchBooksCriteria , int page, int perPage) {
        log.info("Querying book details from database");
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
        Root<Book> classData = criteriaQuery.from(Book.class);
        List<Predicate> predicates = buildPredicates(searchBooksCriteria, criteriaBuilder, classData);
        criteriaQuery.select(classData).where(criteriaBuilder.and( predicates.toArray(new Predicate[] {}))) ;
        List<Book> books  = entityManager.createQuery(criteriaQuery)
                .setFirstResult(page * perPage)
                .setMaxResults(perPage)
                .getResultList();
        return booksMapper.mapToBooks(books) ;
    }

    public List<Predicate> buildPredicates (SearchBooksCriteria searchBooksCriteria ,CriteriaBuilder criteriaBuilder ,Root<Book> classData){
            List<Predicate> predicateList = new ArrayList<Predicate>();
            if(searchBooksCriteria.getAuthor() != null && ! searchBooksCriteria.getAuthor().equalsIgnoreCase("")) {
                predicateList.add(criteriaBuilder.equal(classData.get("author").get("firstName"), searchBooksCriteria.getAuthor() ));
            }
            if(searchBooksCriteria.getBookName() != null && ! searchBooksCriteria.getBookName().equalsIgnoreCase("")) {
                predicateList.add(criteriaBuilder.equal(classData.get("title"), searchBooksCriteria.getBookName() ));
            }
            if(searchBooksCriteria.getBookType() != null && ! searchBooksCriteria.getBookType().equalsIgnoreCase("")) {
                predicateList.add(criteriaBuilder.equal(classData.get("bookType").get("bookTypeDescription"), searchBooksCriteria.getBookType() ));
            }
            if(searchBooksCriteria.getCategory() != null && ! searchBooksCriteria.getCategory().equalsIgnoreCase("")) {
                predicateList.add(criteriaBuilder.equal(classData.get("category").get("categoryName"), searchBooksCriteria.getCategory() ));
            }

        return predicateList;
    }


}
