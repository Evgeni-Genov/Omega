package com.example.omega.service.exception;

import com.example.omega.service.exception.enumeration.ErrorCode;

public class HttpBadRequestException extends DebuggableException {

    public HttpBadRequestException(String debugMessage) {
        super(ErrorCode.BAD_REQUEST, debugMessage);
    }

    public HttpBadRequestException(String debugMessage, Throwable throwable) {
        super(ErrorCode.BAD_REQUEST, debugMessage, throwable);
    }
}
