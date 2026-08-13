package com.quizbotv2.service;

import com.quizbotv2.dto.ChatMessage;
import com.quizbotv2.dto.ChatRequest;
import com.quizbotv2.dto.ChatResponse;
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

    public String getChatResponse(String userPrompt){

        ChatRequest request = new ChatRequest(
                model,
                List.of(new ChatMessage("user", userPrompt))
        );

        ChatResponse response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        if (response == null || response.choices().isEmpty()){
            return "No response from model.";
        }
        return response.choices().get(0).message().content();

    }

}
