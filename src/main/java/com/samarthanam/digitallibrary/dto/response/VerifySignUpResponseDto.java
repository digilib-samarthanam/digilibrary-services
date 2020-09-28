package com.samarthanam.digitallibrary.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VerifySignUpResponseDto {

    @JsonProperty("verification_status")
    private final String verificationStatus;
}
