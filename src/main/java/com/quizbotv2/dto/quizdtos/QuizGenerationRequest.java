package com.quizbotv2.dto.quizdtos;


import com.quizbotv2.dto.enums.Difficulty;

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
