package com.samarthanam.digitallibrary.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"statusCode","errorMessage"})
public class ErrorDetails {

    @JsonProperty("errorMessage")
    private String message;

    @JsonProperty("statusCode")
    private String status;

    public ErrorDetails(String message) {
        this.message = message;
    }
}

