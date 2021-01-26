package com.samarthanam.digitallibrary.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "user_activity_history")
@Data
public class UserActivityHistory {

    @Id
    @Column(name = "user_activity_history_id")
    private Integer userActivityHistoryId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "isbn")
    private Integer isbn;

    @Column(name = "isbn")
    private Integer isbn;

    @Column(name = "current_page")
    private Integer currentPage;

    @Column(name = "active_status")
    private Boolean activeStatus;

    @Column(name = "audio_time")
    private LocalTime audioTime;

    @Column(name = "crte_ts")
    private LocalDateTime createTimestamp;

    @Column(name = "updt_ts")
    private LocalDateTime updateTimestamp;
}