package com.samarthanam.digitallibrary.exception;

import com.samarthanam.digitallibrary.enums.ServiceError;

public class TokenSerializationException extends AbstractServiceException {
    public TokenSerializationException(final ServiceError serviceError) {
        super(serviceError);
    }
}
