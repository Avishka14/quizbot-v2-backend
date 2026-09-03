package com.quizbotv2.dto.userdtos;

public record UserQuestionsDTO(
        Long id,
        String question,
        String correctAnswer,
        String userAnswer
) {
}
