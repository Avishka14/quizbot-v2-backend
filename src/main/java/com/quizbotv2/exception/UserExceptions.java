package com.quizbotv2.exception;

import lombok.Getter;

@Getter
public class UserExceptions extends RuntimeException {
    private final ErrorCode errorCode;

    public UserExceptions(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public UserExceptions(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDefaultMessage(), cause);
        this.errorCode = errorCode;
    }
}