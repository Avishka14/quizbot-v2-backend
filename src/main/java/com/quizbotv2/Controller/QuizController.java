package com.quizbotv2.Controller;

import com.quizbotv2.dto.quizdtos.QuizGenerationRequest;
import com.quizbotv2.dto.quizdtos.QuizResult;
import com.quizbotv2.service.OpenRouterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/chat")
public class QuizController {

    private final OpenRouterService openRouterService;

    public QuizController(OpenRouterService openRouterService) {
        this.openRouterService = openRouterService;
    }

    @PostMapping("/generate")
    public ResponseEntity<QuizResult> generateQuiz(@RequestBody QuizGenerationRequest request) {
        QuizResult result = openRouterService.generateQuiz(request);
        return ResponseEntity.ok(result);
    }
}
