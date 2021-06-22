package com.samarthanam.digitallibrary.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "user_activity_history")
@Data
@IdClass(UserActivityId.class)
public class UserActivityHistory implements Serializable {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn")
    private Book book;

    @Column(name = "current_page")
    private Integer currentPage;

    @Column(name = "audio_time")
    private LocalTime audioTime;

    @Column(name = "crte_ts")
    private LocalDateTime createdTimestamp;

    @Column(name = "updt_ts")
    private LocalDateTime updatedTimestamp;
}