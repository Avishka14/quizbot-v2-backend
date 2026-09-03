package com.quizbotv2.dto.userdtos;

public record UserQuizResultDTO(
        Long id,
        int mark,
        String totalTime,
        int questionCount
) {
}

