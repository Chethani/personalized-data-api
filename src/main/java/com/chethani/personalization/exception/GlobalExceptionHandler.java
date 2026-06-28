package com.chethani.personalization.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.chethani.personalization.dto.ErrorResponse;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String VALIDATION_FAILED = "Validation failed";
    private static final String BAD_REQUEST = "Bad request";
    private static final String UNEXPECTED_ERROR = "Unexpected error occurred";

    // Handles validation errors in request body DTOs, e.g. @Valid @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRequestBodyValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .toList();

        return badRequest(VALIDATION_FAILED, errors);
    }

    // Handles malformed or unreadable JSON request bodies.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRequestBody(HttpMessageNotReadableException ex) {
        return badRequest(
                VALIDATION_FAILED,
                List.of("Invalid request body")
        );
    }

    // Handles request parameter validation errors, e.g. @RequestParam @Min/@Max/@NotBlank
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage())
                .toList();

        return badRequest(VALIDATION_FAILED, errors); // same message can get to constant
    }

    // Handles required query parameters that are completely missing
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestParam(MissingServletRequestParameterException ex) {
        return badRequest(
            VALIDATION_FAILED,
            List.of("Missing required request parameter: " + ex.getParameterName())
        );
    }

    // Handles invalid request parameter type, e.g. limit=abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return badRequest(
            VALIDATION_FAILED,
            List.of(ex.getName() + " must be a valid value")
        );
    }

    // Handles business validation errors thrown manually from service layer
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        return badRequest(BAD_REQUEST, List.of(ex.getMessage()));
    }

    // Fallback handler for unexpected errors
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        log.error(UNEXPECTED_ERROR, ex);

        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                UNEXPECTED_ERROR,
                List.of()
        );
    }

    // Builds a consistent 400 Bad Request response.
    private ErrorResponse badRequest(String message, List<String> errors) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                errors
        );
    }

}