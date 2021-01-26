package com.samarthanam.digitallibrary.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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

    @Column(name = "isbn")
    private Integer isbn;

    @Column(name = "crte_ts")
    private LocalDateTime createdTimestamp;

}