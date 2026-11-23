package com.quizbot.dto.quizdtos;


import com.quizbot.enums.Difficulty;

public record QuizGenerationRequest(
        String topic,
        Difficulty difficulty,
        int questionCount

) {

    // testing method for developments
    public static QuizGenerationRequest testingRequest(String topic){
        return new QuizGenerationRequest(topic, Difficulty.EASY, 3);
    }


}
