package com.quizbot.exception;

import lombok.Getter;

@Getter
public class QuizGenerationException extends RuntimeException {

    private final ErrorCode errorCode;

    public QuizGenerationException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    // detail is for logs only; the client gets the default message
    public QuizGenerationException(ErrorCode errorCode, String detail) {
        super(detail);
        this.errorCode = errorCode;
    }

    public QuizGenerationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDefaultMessage(), cause);
        this.errorCode = errorCode;
    }
}
