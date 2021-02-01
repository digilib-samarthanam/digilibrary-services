package com.samarthanam.digitallibrary.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_type_format")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookType {

    @Id
    @Column(name = "book_type_code")
    private Integer bookTypeCode;

    @Column(name = "book_type_description")
    private String bookTypeDescription;

    @Column(name = "update_ts")
    private LocalDateTime updatedTimestamp;

    @Column(name = "create_ts")
    private LocalDateTime createdTimestamp;

}