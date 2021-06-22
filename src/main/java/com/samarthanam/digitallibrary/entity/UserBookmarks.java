package com.samarthanam.digitallibrary.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "user_bookmarks")
@Data
@IdClass(UserActivityId.class)
public class UserBookmarks {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn")
    private Book book;

    @Column(name = "crte_ts")
    private LocalDateTime createdTimestamp;

    @Column(name = "current_page")
    private Integer currentPage;

    @Column(name = "audio_time")
    private LocalTime audioTime;

}