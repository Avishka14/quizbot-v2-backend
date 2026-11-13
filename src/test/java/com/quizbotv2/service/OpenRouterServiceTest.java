package com.quizbotv2.service;

import com.quizbotv2.dto.quizdtos.QuizGenerationRequest;
import com.quizbotv2.dto.quizdtos.QuizResult;
import com.quizbotv2.exception.QuizGenerationException;
import com.quizbotv2.helper.PromptBuildHelper;
import com.quizbotv2.helper.QuizValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenRouterServiceTest {

    @Mock private ObjectMapper objectMapper;
    @Mock private PromptBuildHelper promptBuildHelper;
    @Mock private QuizValidator quizValidator;
    @Mock private QuizGenerationRequest request;
    @Mock private QuizResult quizResult;

    // ---------- helpers ----------

    private OpenRouterService serviceWith(Mono<ClientResponse> exchangeResult) {
        WebClient webClient = WebClient.builder()
                .exchangeFunction(req -> exchangeResult)
                .build();

        OpenRouterService service =
                new OpenRouterService(webClient, objectMapper, promptBuildHelper, quizValidator);

        // @Value fields are not injected without Spring, so set them manually
        ReflectionTestUtils.setField(service, "model", "test-model");
        ReflectionTestUtils.setField(service, "fallbackModels", "fallback-model");
        return service;
    }

    private Mono<ClientResponse> jsonResponse(HttpStatus status, String body) {
        return Mono.just(ClientResponse.create(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .build());
    }

    // ---------- tests ----------

    @Test
    void generateQuiz_returnsValidatedResult_onSuccess() {
        String body = """
                {"choices":[{"message":{"role":"assistant","content":"raw-json"}}]}
                """;

        when(request.topic()).thenReturn("Java");
        when(promptBuildHelper.sanitizeJsonResponse("raw-json")).thenReturn("clean-json");
        when(quizValidator.parseQuizResponse("clean-json", "Java", objectMapper)).thenReturn(quizResult);

        QuizResult result = serviceWith(jsonResponse(HttpStatus.OK, body)).generateQuiz(request);

        assertSame(quizResult, result);
        verify(quizValidator).validateQuiz(quizResult);
    }

    @Test
    void generateQuiz_throws_whenUpstreamReturnsServerError() {
        OpenRouterService service = serviceWith(jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR, "{\"error\":\"boom\"}"));

        assertThrows(QuizGenerationException.class, () -> service.generateQuiz(request));
    }

    @Test
    void generateQuiz_throws_whenRateLimited() {
        OpenRouterService service = serviceWith(jsonResponse(HttpStatus.TOO_MANY_REQUESTS, "{\"error\":\"slow down\"}"));

        assertThrows(QuizGenerationException.class, () -> service.generateQuiz(request));
    }

    @Test
    void generateQuiz_throws_whenChoicesEmpty() {
        OpenRouterService service = serviceWith(jsonResponse(HttpStatus.OK, "{\"choices\":[]}"));

        assertThrows(QuizGenerationException.class, () -> service.generateQuiz(request));
    }

    @Test
    void generateQuiz_throws_whenUpstreamUnreachable() {
        Mono<ClientResponse> connectionError = Mono.error(new WebClientRequestException(
                new java.net.ConnectException("refused"),
                HttpMethod.POST,
                URI.create("http://localhost/chat/completions"),
                new HttpHeaders()));

        OpenRouterService service = serviceWith(connectionError);

        assertThrows(QuizGenerationException.class, () -> service.generateQuiz(request));
    }
}