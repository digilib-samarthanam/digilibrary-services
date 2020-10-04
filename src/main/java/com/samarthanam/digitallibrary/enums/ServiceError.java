package com.samarthanam.digitallibrary.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ServiceError {

    USER_ALREADY_EXIST("ERR_01", "User already exist, please login to continue!", HttpStatus.CONFLICT),
    TOKEN_INVALID("ERR_02", "Token invalid!", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("ERR_03", "Token expired!", HttpStatus.UNAUTHORIZED),
    TOKEN_SERIALIZATION_ERROR("ERR_04", "Error while serializing token", HttpStatus.INTERNAL_SERVER_ERROR),
    RESOURCE_NOT_FOUND("ERR_05", "Requested resource was not found", HttpStatus.INTERNAL_SERVER_ERROR),
    CREDENTIAL_MISMATCH("ERR_06", "INVALID EMAIL/PASSWORD", HttpStatus.UNAUTHORIZED),
    USER_NOT_VERIFIED("ERR_07", "Please verify yourself using email sent", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("ERR_08", "No user found with this email id", HttpStatus.NOT_FOUND);

    private final String errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;
}
