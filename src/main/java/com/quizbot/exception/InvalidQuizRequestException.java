package com.quizbot.exception;

import lombok.Getter;

@Getter
public class InvalidQuizRequestException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidQuizRequestException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }
}
