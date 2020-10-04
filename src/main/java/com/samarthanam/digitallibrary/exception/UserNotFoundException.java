package com.samarthanam.digitallibrary.exception;

import com.samarthanam.digitallibrary.enums.ServiceError;

public class UserNotFoundException extends AbstractServiceException {
    public UserNotFoundException(final ServiceError serviceError) {
        super(serviceError);
    }
}