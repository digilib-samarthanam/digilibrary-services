package com.samarthanam.digitallibrary.handler;

import com.samarthanam.digitallibrary.dto.response.GenericErrorResponseDto;
import com.samarthanam.digitallibrary.exception.AbstractServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.xml.bind.ValidationException;
import java.util.NoSuchElementException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(AbstractServiceException.class)
    protected ResponseEntity<GenericErrorResponseDto> handleCustomExceptions(AbstractServiceException e) {

        String errorCode = e.getServiceError().getErrorCode();
        String errorMessage = e.getServiceError().getErrorMessage();
        HttpStatus httpStatus = e.getServiceError().getHttpStatus();

        GenericErrorResponseDto errorResponseDTO = new GenericErrorResponseDto(errorMessage, errorCode);
        return new ResponseEntity<>(errorResponseDTO, httpStatus);
    }

    @ExceptionHandler({
            MissingRequestHeaderException.class,
            NoSuchElementException.class,
            ValidationException.class,
            MethodArgumentTypeMismatchException.class
    })
    public final ResponseEntity<GenericErrorResponseDto> handleException(Exception e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(new GenericErrorResponseDto(e.getMessage(), "400"), HttpStatus.BAD_REQUEST);
    }
}
