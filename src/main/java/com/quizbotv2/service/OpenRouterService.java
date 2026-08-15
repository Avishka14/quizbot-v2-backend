package com.quizbotv2.service;

import com.quizbotv2.dto.QuizTopic;
import com.quizbotv2.dto.QuizRequest;
import com.quizbotv2.dto.QuizResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class OpenRouterService {

    private final WebClient webClient;

    @Value("${openrouter.model}")
    private String model;

    public OpenRouterService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String getQuizResponse(String quizTopic){

        QuizRequest request = new QuizRequest(
                model,
                List.of(new QuizTopic("user", quizTopic))
        );

        QuizResponse response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(QuizResponse.class)
                .block();

        if (response == null || response.choices().isEmpty()){
            return "No response from model.";
        }
        return response.choices().get(0).message().content();

    }

}
