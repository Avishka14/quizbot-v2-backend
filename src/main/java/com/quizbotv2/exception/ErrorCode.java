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
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GEN_500", "Something went wrong."),

    QUIZ_INVALID_JSON(HttpStatus.BAD_GATEWAY, "QUIZ_001", "Quiz response was not valid JSON."),
    QUIZ_MISSING_QUESTIONS(HttpStatus.BAD_GATEWAY, "QUIZ_002", "Quiz response has no questions array."),
    QUIZ_ZERO_QUESTIONS(HttpStatus.BAD_GATEWAY, "QUIZ_003", "Quiz contains no questions."),
    QUIZ_INVALID_OPTIONS(HttpStatus.BAD_GATEWAY, "QUIZ_004", "A question does not have exactly 4 options."),
    QUIZ_INVALID_ANSWER(HttpStatus.BAD_GATEWAY, "QUIZ_005", "A question's correct answer is not among its options."),
    QUIZ_START_TIME_END_TIME_MISSING(HttpStatus.BAD_REQUEST, "QUIZ_006", "Quiz start time and end time are required to calculate the result"),
    QUIZ_START_TIME_INVALID(HttpStatus.BAD_REQUEST, "QUIZ_007", "Quiz end time cannot be before start time");



    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

}
