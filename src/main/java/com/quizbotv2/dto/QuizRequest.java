package com.quizbotv2.dto;

import java.util.List;

public record QuizRequest(
        String model,
        List<QuizTopic> messages
) {}
