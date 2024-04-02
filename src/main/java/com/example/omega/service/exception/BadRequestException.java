package com.example.omega.service.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException {

    private final HttpStatus status;

    public BadRequestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public BadRequestException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public HttpStatus getStatus() {
        return status;
    }

}
