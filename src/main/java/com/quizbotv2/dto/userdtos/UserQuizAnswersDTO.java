package com.quizbotv2.dto.userdtos;

import com.quizbotv2.model.Question;

public record UserQuizAnswersDTO(
        Long id,
        Question question,
        boolean isCorrect,
        String correctAnswer,
        String userAnswer
) {
}
