package com.quizbotv2.dto.quizdtos;

import java.util.List;

public record QuizRequest(
        String model,
        List<QuizTopic> messages
) {}
