package com.example.omega.service.exception;

import com.example.omega.service.exception.enumeration.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebuggableException extends RuntimeException{

    private final ErrorCode errorCode;
    private final String debugMessage;

    public DebuggableException(ErrorCode errorCode, String debugMessage){
        this.errorCode = errorCode;
        this.debugMessage = debugMessage;
    }

    public DebuggableException(ErrorCode errorCode, String debugMessage, Throwable throwable){
        super(throwable);
        this.errorCode = errorCode;
        this.debugMessage = debugMessage;
    }
}
