package com.samarthanam.digitallibrary.exception;

import com.samarthanam.digitallibrary.enums.ServiceError;

public class TokenTemperedException extends AbstractServiceException {
    public TokenTemperedException(final ServiceError serviceError) {
        super(serviceError);
    }
}
