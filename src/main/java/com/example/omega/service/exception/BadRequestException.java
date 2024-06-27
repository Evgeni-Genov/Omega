package com.example.omega.service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class BadRequestException extends RuntimeException {

    private final HttpStatus status;
    private final Map<String, String> validationErrors;

    public BadRequestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.validationErrors = null;
    }

    public BadRequestException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message, Map<String, String> validationErrors) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.validationErrors = validationErrors;
    }

}