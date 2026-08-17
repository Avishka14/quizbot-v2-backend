package com.quizbotv2.dto.quizdtos;

import java.util.List;

public record QuizResponse(
        List<Choice> choices) {
         public record Choice(
                 QuizTopic message)
         {}
}
