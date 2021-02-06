package com.samarthanam.digitallibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
public class VerifySignUpDto {

    @NotNull
    private final String token;
}
