package com.samarthanam.digitallibrary.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class User {

    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String emailAddress;
    private String password;


}
