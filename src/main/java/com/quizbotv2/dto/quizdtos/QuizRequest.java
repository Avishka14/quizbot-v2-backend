package com.quizbotv2.dto.quizdtos;

import java.util.List;

public record QuizRequest(
        String model,
        List<String> models,
        List<QuizTopic> messages
) {}
