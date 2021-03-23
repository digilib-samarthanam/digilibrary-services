package com.samarthanam.digitallibrary.entity;

import com.samarthanam.digitallibrary.constant.BookType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_type_format")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookTypeFormat {

    @Id
    @Column(name = "book_type_code")
    private Integer bookTypeCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "book_type_description")
    private BookType bookTypeDescription;

    @Column(name = "update_ts")
    private LocalDateTime updatedTimestamp;

    @Column(name = "create_ts")
    private LocalDateTime createdTimestamp;

}