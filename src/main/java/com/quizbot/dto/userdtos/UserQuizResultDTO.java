package com.quizbot.dto.userdtos;

import java.util.UUID;

public record UserQuizResultDTO(
        UUID id,
        String mark,
        String totalTime,
        int questionCount
) {

}