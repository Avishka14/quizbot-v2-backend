package com.quizbotv2.dto.quizdtos;

import java.util.List;

public record QuizResult(
        String topic,
        List<QuizQuestion> questions
) {
}
