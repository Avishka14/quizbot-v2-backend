package com.quizbot.dto.userdtos;

public record UserQuizAnswersDTO(
        Long id,
        String question,
        String correctAnswer
) {
}
