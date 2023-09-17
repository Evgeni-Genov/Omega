package com.example.omega.service.exception.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "GENERAL_BAD_REQUEST"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "GENERAL_NOT_FOUND"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GENERAL_SERVER_ERROR"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "GENERAL_METHOD_NOT_ALLOWED"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");

    private final HttpStatus httpStatus;
    public final String errorCode;

    public static ErrorCode fromHttpStatus(final HttpStatus httpStatus){
        return Arrays.stream(ErrorCode.values())
                .filter(e -> e.getHttpStatus() == httpStatus)
                .findFirst().orElse(ErrorCode.SERVER_ERROR);
    }

}
