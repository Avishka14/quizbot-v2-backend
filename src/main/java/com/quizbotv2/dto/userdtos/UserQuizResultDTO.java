package com.quizbotv2.dto.userdtos;

import java.util.UUID;

public record UserQuizResultDTO(
        UUID id,
        String mark,
        String totalTime,
        int questionCount
) {

}