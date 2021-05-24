package com.samarthanam.digitallibrary.dto.response;

import lombok.Data;

@Data
public class BookActivityStatusRequest {

    private Integer isbn;
    private Integer userId;
    private Integer currentPage;
    private String audioTime;

}