package com.samarthanam.digitallibrary.dto.request;

import lombok.Getter;

@Getter
public class UserSignupRequestDto {

    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String emailAddress;
    private String password;

}
