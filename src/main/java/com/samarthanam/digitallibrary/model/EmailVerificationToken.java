package com.samarthanam.digitallibrary.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailVerificationToken extends AbstractToken {

    @JsonProperty("userSequenceId")
    private final Integer userSequenceId;
}
