package com.samarthanam.digitallibrary.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @Id
    @Column(name = "isbn")
    private Integer isbn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_type_code")
    private BookTypeFormat bookTypeFormat;

    @Column(name = "title")
    private String title;

    @Column(name = "year")
    private String year;

    @Column(name = "country_of_origin")
    private String countryOfOrigin;

    @Column(name = "edition_version")
    private String editionVersion;

    @Column(name = "update_ts")
    private LocalDateTime updatedTimestamp;

    @Column(name = "create_ts")
    private LocalDateTime createdTimestamp;

    @Column(name = "total_pages")
    private Integer totalPages;

    @Column(name = "total_audio_time")
    private LocalTime totalAudioTime;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "active")
    private Boolean active;

    public String getFileName() {
        return bookTypeFormat.getBookTypeDescription().fileNameWithExtension(fileName);
    }

    public String getThumbnailFileName() {
        return fileName + ".jpg";
    }
}