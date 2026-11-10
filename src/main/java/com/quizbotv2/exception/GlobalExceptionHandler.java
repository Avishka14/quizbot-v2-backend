package com.quizbotv2.exception;

import com.quizbotv2.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTopicException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTopic(InvalidTopicException ex) {
        return build(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return build(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.getDefaultMessage());
    }

    @ExceptionHandler(QuizGenerationException.class)
    public ResponseEntity<ErrorResponse> handleQuizGeneration(QuizGenerationException ex) {
        log.warn("Quiz generation failed [{}]: {}", ex.getErrorCode().getCode(), ex.getMessage());
        // default message to client; question text and model output stay in logs
        return build(ex.getErrorCode(), ex.getErrorCode().getDefaultMessage());
    }

    @ExceptionHandler(InvalidQuizRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQuizRequest(InvalidQuizRequestException ex) {
        return build(ex.getErrorCode(), ex.getErrorCode().getDefaultMessage());
    }

    private ResponseEntity<ErrorResponse> build(ErrorCode code, String message) {
        ErrorResponse body = ErrorResponse.builder()
                .code(code.getCode())
                .message(message)
                .status(code.getStatus().value())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(code.getStatus()).body(body);
    }
}
