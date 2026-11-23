package com.quizbot.dto.userdtos;

public record UserQuestionsDTO(
        Long id,
        String correctAnswer,
        String userAnswer
) {
}
