package com.samarthanam.digitallibrary.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookActivityStatus {

    private BookResponse book;
    private Integer currentPage;
    private Boolean activeStatus;
    private LocalTime audioTime;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;

}