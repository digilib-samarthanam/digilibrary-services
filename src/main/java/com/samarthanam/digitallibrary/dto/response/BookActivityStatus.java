package com.samarthanam.digitallibrary.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class BookActivityStatus {

    private BookResponse book;
    private Integer currentPage;
    private Boolean activeStatus;
    private LocalTime audioTime;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;

}