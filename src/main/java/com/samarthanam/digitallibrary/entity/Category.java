package com.samarthanam.digitallibrary.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_literature_category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @Column(name = "category_id")
    @SequenceGenerator(name = "seq", sequenceName = "category_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private Integer categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "create_ts")
    private LocalDateTime createdTimestamp;

    @Formula("(SELECT COUNT(*) FROM book_sub_category sb WHERE sb.category_id= category_id)")
    private Long subCategoriesCount;

}