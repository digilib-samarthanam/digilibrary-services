package com.samarthanam.digitallibrary.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ForgotPasswordRequestDto {

    @NotBlank
    private String email;
}
