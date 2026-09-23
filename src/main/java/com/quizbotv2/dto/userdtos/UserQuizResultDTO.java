package com.quizbotv2.dto.userdtos;

import java.util.UUID;

public record UserQuizResultDTO(
        UUID id,
        String mark,
        String totalTime,
        int questionCount
) {

    public UUID getId() {
        return id;
    }

    public String getMark() {
        return mark;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public int getQuestionCount() {
        return questionCount;
    }
}