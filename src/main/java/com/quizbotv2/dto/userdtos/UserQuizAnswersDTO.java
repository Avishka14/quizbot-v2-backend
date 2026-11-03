package com.quizbotv2.dto.userdtos;

import com.quizbotv2.model.Question;

public record UserQuizAnswersDTO(
        Long id,
        String question,
        String correctAnswer
) {
}
