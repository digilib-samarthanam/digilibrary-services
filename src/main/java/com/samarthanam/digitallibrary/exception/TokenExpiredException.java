package com.samarthanam.digitallibrary.exception;

import com.samarthanam.digitallibrary.enums.ServiceError;

public class TokenExpiredException extends AbstractServiceException {
    public TokenExpiredException(final ServiceError serviceError) {
        super(serviceError);
    }
}
