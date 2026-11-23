package com.quizbot.dto.quizdtos;

import java.util.List;

public record QuizQuestion(
        int id,
        String question,
        List<String> options,
        String correctAnswer
) {

}
