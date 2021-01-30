package com.samarthanam.digitallibrary.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_bookmarks")
@Data
public class UserBookmarks {

    @Id
    @Column(name = "user_bookmarks_id")
    private Integer userBookmarksId;

    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn")
    private Book book;

    @Column(name = "crte_ts")
    private LocalDateTime createdTimestamp;

}