package com.quizbot.exception;

import lombok.Getter;

@Getter
public class InvalidTopicException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidTopicException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public InvalidTopicException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
