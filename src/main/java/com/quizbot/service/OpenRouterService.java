package com.quizbot.service;

import com.quizbot.dto.quizdtos.QuizRequest;
import com.quizbot.dto.quizdtos.QuizResponse;
import com.quizbot.dto.quizdtos.QuizResult;
import com.quizbot.dto.quizdtos.QuizTopic;
import com.quizbot.dto.quizdtos.QuizGenerationRequest;
import com.quizbot.exception.ErrorCode;
import com.quizbot.exception.QuizGenerationException;
import com.quizbot.helper.PromptBuildHelper;
import com.quizbot.helper.QuizValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import tools.jackson.databind.ObjectMapper;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OpenRouterService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final PromptBuildHelper promptBuildHelper;
    private final QuizValidator quizValidator;

    @Value("${openrouter.model}")
    private String model;

    @Value("${openrouter.fallback-models}")
    private String fallbackModels;

    public OpenRouterService(WebClient webClient, ObjectMapper objectMapper, PromptBuildHelper promptBuildHelper, QuizValidator quizValidator) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.promptBuildHelper = promptBuildHelper;
        this.quizValidator = quizValidator;
    }

    public QuizResult generateQuiz(QuizGenerationRequest request){

        List<String> models  = new ArrayList<>();
        models.add(model);
        models.add(fallbackModels);

        QuizRequest apiReqesut = new QuizRequest(
                model,
                models,
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

        QuizResponse response;
        try {
            response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(apiRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, resp ->
                            resp.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .map(body -> {
                                        log.error("OpenRouter {} -> {}", resp.statusCode(), body);
                                        ErrorCode code = resp.statusCode().value() == 429
                                                ? ErrorCode.QUIZ_UPSTREAM_RATE_LIMITED
                                                : ErrorCode.QUIZ_UPSTREAM_ERROR;
                                        // body is logged only, never sent to the client
                                        return new QuizGenerationException(
                                                code, "OpenRouter " + resp.statusCode() + ": " + body);
                                    }))
                    .bodyToMono(QuizResponse.class)
                    .block();
        } catch (WebClientRequestException e) {
            // connection refused, DNS failure, read/connect timeout (no HTTP response at all)
            log.error("Could not reach OpenRouter for model: {}", model, e);
            throw new QuizGenerationException(ErrorCode.QUIZ_UPSTREAM_UNREACHABLE, e);
        }

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            log.error("No response received from OpenRouter model: {}", model);
            throw new QuizGenerationException(ErrorCode.QUIZ_EMPTY_MODEL_RESPONSE);
        }

        return response.choices().get(0).message().content();
    }

}
