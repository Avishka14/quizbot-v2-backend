package com.quizbotv2.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    TOPIC_EMPTY(HttpStatus.BAD_REQUEST, "TOPIC_001", "Topic must not be empty."),
    TOPIC_LENGTH(HttpStatus.BAD_REQUEST, "TOPIC_002", "Topic length is invalid."),
    TOPIC_TOO_MANY_WORDS(HttpStatus.BAD_REQUEST, "TOPIC_003", "Topic has too many words."),
    TOPIC_INVALID_CHARS(HttpStatus.BAD_REQUEST, "TOPIC_004", "Topic contains invalid characters."),
    TOPIC_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "TOPIC_005", "Topic is not allowed."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GEN_500", "Something went wrong.");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

}
