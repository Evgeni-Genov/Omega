package com.example.omega.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private HttpStatus httpStatus;

    private String message;

    private String timestamp;
}
