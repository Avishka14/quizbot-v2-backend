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
    QUIZ_START_TIME_INVALID(HttpStatus.BAD_REQUEST, "QUIZ_007", "Quiz end time cannot be before start time"),


    QUIZ_UPSTREAM_ERROR(HttpStatus.BAD_GATEWAY, "QUIZ_008", "The quiz model returned an error."),
    QUIZ_UPSTREAM_RATE_LIMITED(HttpStatus.SERVICE_UNAVAILABLE, "QUIZ_009", "The quiz model is busy. Please try again shortly."),
    QUIZ_UPSTREAM_UNREACHABLE(HttpStatus.SERVICE_UNAVAILABLE, "QUIZ_010", "The quiz model could not be reached."),
    QUIZ_EMPTY_MODEL_RESPONSE(HttpStatus.BAD_GATEWAY, "QUIZ_011", "The quiz model returned no response."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "User not found."),
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "QUIZ_012", "Quiz not found."),
    QUIZ_INPUT_REQUIRED(HttpStatus.BAD_REQUEST, "QUIZ_013", "Quiz input is required to calculate the result."),
    QUIZ_IDS_REQUIRED(HttpStatus.BAD_REQUEST, "QUIZ_014", "User ID and quiz ID are required."),
    QUIZ_NO_QUESTIONS_SUBMITTED(HttpStatus.BAD_REQUEST, "QUIZ_015", "At least one question is required to calculate the result."),
    QUIZ_EMPTY_RESULT(HttpStatus.BAD_GATEWAY, "QUIZ_016", "Quiz generation returned an empty result."),
    QUIZ_NO_VALID_QUESTIONS(HttpStatus.BAD_GATEWAY, "QUIZ_017", "The generated quiz had no valid questions."),
    QUIZ_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "QUIZ_018", "Could not save the quiz. Please try again."),
    QUIZ_MARKS_NOTFOUND(HttpStatus.NOT_FOUND, "QUIZ_019", "Marks Not found for the Quiz.");


    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

}
