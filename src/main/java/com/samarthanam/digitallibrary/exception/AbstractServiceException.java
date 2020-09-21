package com.samarthanam.digitallibrary.exception;

import com.samarthanam.digitallibrary.enums.ServiceError;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractServiceException extends Exception {

    protected final ServiceError serviceError;
}
