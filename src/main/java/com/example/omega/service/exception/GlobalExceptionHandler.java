package com.example.omega.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.omega.service.util.Constants.DATE_FORMATTER;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        var formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        var formattedInstant = formatter.format(LocalDateTime.now());
        var errorResponse = new ErrorResponse(ex.getStatus(), ex.getMessage(), formattedInstant);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<Object> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        var formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        var formattedInstant = formatter.format(LocalDateTime.now());
        var errorResponse = new ErrorResponse((HttpStatus) ex.getStatusCode(), ex.getMessage(), formattedInstant);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNotFoundException(NoHandlerFoundException ex) {
        var formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        var formattedInstant = formatter.format(LocalDateTime.now());
        var errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), formattedInstant);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleInternalServerErrorException(Exception ex) {
        var formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        var formattedInstant = formatter.format(LocalDateTime.now());
        var errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getCause().getCause().toString(), formattedInstant);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleForbiddenException(AccessDeniedException ex) {
        var formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        var formattedInstant = formatter.format(LocalDateTime.now());
        var errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), formattedInstant);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    //TODO: test
    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleUnauthorizedException(HttpClientErrorException.Unauthorized ex) {
        var formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        var formattedInstant = formatter.format(LocalDateTime.now());
        var errorResponse = new ErrorResponse((HttpStatus) ex.getStatusCode(), ex.getMessage(), formattedInstant);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}
