package com.samarthanam.digitallibrary.model;

import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
public class UserLoginToken extends AbstractToken {

    private final String firstName;
    private final String lastName;
    private final String gender;
    private final String emailAddress;
    private final Integer userSequenceId;
    private final boolean isAdmin;
}
