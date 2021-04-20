package com.samarthanam.digitallibrary.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class BookActivityStatusRequest {

    private Integer isbn;
    private Integer userId;
    private Integer currentPage;
//    private Boolean activeStatus;
    private LocalTime audioTime;
//    private LocalDateTime updatedTimestamp;

}