package com.quizbot.exception.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class ErrorResponse {
    String code;
    String message;
    int status;
    Instant timestamp;
}