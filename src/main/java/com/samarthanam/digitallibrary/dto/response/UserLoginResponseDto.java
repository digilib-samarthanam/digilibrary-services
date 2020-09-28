package com.samarthanam.digitallibrary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserLoginResponseDto {

    private final long timeStamp = System.currentTimeMillis();
    private String token;
}
