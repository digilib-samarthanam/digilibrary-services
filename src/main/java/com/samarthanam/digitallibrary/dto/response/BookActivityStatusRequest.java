package com.samarthanam.digitallibrary.dto.response;

import lombok.Data;

import java.time.LocalTime;

@Data
public class BookActivityStatusRequest {

    private Integer isbn;
    private Integer userId;
    private Integer currentPage;
    private LocalTime audioTime;

}