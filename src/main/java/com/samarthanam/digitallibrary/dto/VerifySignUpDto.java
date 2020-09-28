package com.samarthanam.digitallibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VerifySignUpDto {
    private final String token;
}
