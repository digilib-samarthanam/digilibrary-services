package com.samarthanam.digitallibrary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GenericErrorResponseDto {

    private final long timestamp = System.currentTimeMillis();
    private final String errorMessage;
    private final String errorCode;

}
