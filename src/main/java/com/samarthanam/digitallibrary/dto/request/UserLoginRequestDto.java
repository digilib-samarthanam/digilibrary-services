package com.samarthanam.digitallibrary.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class UserLoginRequestDto {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
