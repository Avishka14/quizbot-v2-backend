package com.quizbotv2.service;

import com.quizbotv2.dto.quizdtos.QuizRequest;
import com.quizbotv2.dto.quizdtos.QuizResponse;
import com.quizbotv2.dto.quizdtos.QuizResult;
import com.quizbotv2.dto.quizdtos.QuizTopic;
import com.quizbotv2.dto.quizdtos.QuizGenerationRequest;
import com.quizbotv2.exception.QuizGenerationException;
import com.quizbotv2.helper.PromptBuildHelper;
import com.quizbotv2.helper.QuizValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;


import java.util.List;

@Service
public class OpenRouterService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final PromptBuildHelper promptBuildHelper;
    private final QuizValidator quizValidator;

    @Value("${openrouter.model}")
    private String model;

    public OpenRouterService(WebClient webClient, ObjectMapper objectMapper, PromptBuildHelper promptBuildHelper, QuizValidator quizValidator) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.promptBuildHelper = promptBuildHelper;
        this.quizValidator = quizValidator;
    }

    public QuizResult generateQuiz(QuizGenerationRequest request){

        QuizRequest apiReqesut = new QuizRequest(
                model,
                List.of(
                        new QuizTopic("system", promptBuildHelper.buildSystemPrompt()), //instructions for Ai Model
                        new QuizTopic("user", promptBuildHelper.buildUserPrompt(request)) //actual quiz request
                )
        );

        String rawContent = callModel(apiReqesut);
        String cleanJson = promptBuildHelper.sanitizeJsonResponse(rawContent);
        QuizResult result = quizValidator.parseQuizResponse(cleanJson, request.topic(), objectMapper);
        quizValidator.validateQuiz(result);
        return result;

    }

    private String callModel(QuizRequest apiRequest){

        QuizResponse response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(apiRequest) // Spring/WebClient converts java object to a JSON automatically
                .retrieve()
                .bodyToMono(QuizResponse.class)
                .block();

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new QuizGenerationException("No response from model.");
        }

        return response.choices().get(0).message().content();
    }

}
