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
