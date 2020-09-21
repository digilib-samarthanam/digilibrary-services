package com.samarthanam.digitallibrary.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.samarthanam.digitallibrary.dto.response.GenericErrorResponseDto;
import com.samarthanam.digitallibrary.exception.AbstractServiceException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(AbstractServiceException.class)
    protected ResponseEntity<GenericErrorResponseDto> handleCustomExceptions(AbstractServiceException e) {

        String errorCode = e.getServiceError().getErrorCode();
        String errorMessage = e.getServiceError().getErrorMessage();
        HttpStatus httpStatus = e.getServiceError().getHttpStatus();

        GenericErrorResponseDto errorResponseDTO = new GenericErrorResponseDto(errorCode, errorMessage);
        return new ResponseEntity<>(errorResponseDTO, httpStatus);
    }
}
