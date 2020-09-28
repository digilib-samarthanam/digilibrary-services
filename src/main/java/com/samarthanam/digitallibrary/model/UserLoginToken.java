package com.samarthanam.digitallibrary.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
public class UserLoginToken extends AbstractToken {

    @JsonProperty("firstName")
    private final String firstName;

    @JsonProperty("lastName")
    private final String lastName;

    @JsonProperty("gender")
    private final String gender;

    @JsonProperty("mobileNumber")
    private final String mobileNumber;

    @JsonProperty("emailAddress")
    private final String emailAddress;

    @JsonProperty("userSequenceId")
    private final Integer userSequenceId;
}
