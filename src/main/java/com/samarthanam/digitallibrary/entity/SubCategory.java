package com.samarthanam.digitallibrary.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_sub_category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubCategory {
    @Id
    @Column(name = "sub_category_id")
    @SequenceGenerator(name = "seq", sequenceName = "sub_category_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private Integer subCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "sub_category_name")
    private String subCategoryName;

    @Column(name = "create_ts")
    private LocalDateTime createdTimestamp;


}
