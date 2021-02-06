package com.samarthanam.digitallibrary.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class UserSignupRequestDto {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String emailAddress;

    @NotBlank
    private String password;

    @NotBlank
    private String gender;

}
