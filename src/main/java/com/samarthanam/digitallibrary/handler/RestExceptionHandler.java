package com.samarthanam.digitallibrary.handler;

import com.samarthanam.digitallibrary.dto.response.GenericErrorResponseDto;
import com.samarthanam.digitallibrary.exception.AbstractServiceException;
import com.samarthanam.digitallibrary.exception.DuplicateBookmarkRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ValidationException;
import java.util.List;
import java.util.NoSuchElementException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler {


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
            MethodArgumentTypeMismatchException.class,
            DuplicateBookmarkRequestException.class
    })
    public final ResponseEntity<GenericErrorResponseDto> handleException(Exception e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(new GenericErrorResponseDto(e.getMessage(), "400"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<GenericErrorResponseDto> handleException(MethodArgumentNotValidException e) {

        var sb = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for(FieldError fieldError: fieldErrors){
            sb.append(fieldError.getField());
            sb.append(" ");
            sb.append(fieldError.getDefaultMessage());
            sb.append(";");
        }

        log.warn(sb.toString());
        return new ResponseEntity<>(new GenericErrorResponseDto(sb.toString(), "400"), HttpStatus.BAD_REQUEST);
    }

}
