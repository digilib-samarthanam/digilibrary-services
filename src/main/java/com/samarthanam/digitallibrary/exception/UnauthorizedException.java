package com.samarthanam.digitallibrary.exception;

import com.samarthanam.digitallibrary.enums.ServiceError;

public class UnauthorizedException extends AbstractServiceException {
    public UnauthorizedException(final ServiceError serviceError) {
        super(serviceError);
    }
}
