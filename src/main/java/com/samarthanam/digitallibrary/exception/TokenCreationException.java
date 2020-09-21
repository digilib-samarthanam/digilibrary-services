package com.samarthanam.digitallibrary.exception;

import com.samarthanam.digitallibrary.enums.ServiceError;

public class TokenCreationException extends AbstractServiceException {
    public TokenCreationException(final ServiceError serviceError) {
        super(serviceError);
    }
}
