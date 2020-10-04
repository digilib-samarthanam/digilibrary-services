package com.samarthanam.digitallibrary.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ForgotPasswordToken extends AbstractToken {

    @JsonProperty("email")
    private final String email;
}
