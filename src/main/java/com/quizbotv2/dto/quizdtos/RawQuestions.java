package com.quizbotv2.dto.quizdtos;

import java.util.List;

public record RawQuestions(
        String question,
        List<String> options,
        String correctAnswer
) {
}
