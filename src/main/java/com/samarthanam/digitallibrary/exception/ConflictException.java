package com.samarthanam.digitallibrary.exception;

import com.samarthanam.digitallibrary.enums.ServiceError;

public class ConflictException extends AbstractServiceException {

    public ConflictException(final ServiceError serviceError) {
        super(serviceError);
    }
}
