package com.quizbot.dto.quizdtos;

import java.util.List;

public record QuizRequest(
        String model,
        List<String> models,
        List<QuizTopic> messages
) {}
