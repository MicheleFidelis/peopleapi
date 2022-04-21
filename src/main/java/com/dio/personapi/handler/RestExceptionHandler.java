package com.dio.personapi.handler;

import com.dio.personapi.exception.ResourceNotFoundException;
import com.dio.personapi.exception.ExceptionDetails;
import com.dio.personapi.exception.ValidationExceptionsDetails;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Log4j2
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionDetails> handlePersonNotFoundExceptionResponseEntity
            (ResourceNotFoundException ex) {

        ExceptionDetails exceptionDetails = ExceptionDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .title("Person not found")
                .details(ex.getMessage())
                .debugMessage(ex.getClass().getName())
                .build();

        return new ResponseEntity<>(exceptionDetails, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String fields = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));
        String fieldsMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));

        ValidationExceptionsDetails validationExceptionsDetails = ValidationExceptionsDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Invalid fields")

                .details(ex.getMessage())
                .debugMessage(ex.getClass().getName())
                .fields(fields)
                .fieldsMessage(fieldsMessage)
                .build();

        return new ResponseEntity<>(validationExceptionsDetails, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ExceptionDetails exceptionDetails = ExceptionDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .title(ex.getCause().getMessage())
                .details(ex.getMessage())
                .debugMessage(ex.getClass().getName())
                .build();

        return new ResponseEntity<>(exceptionDetails, headers, status);
    }
}
