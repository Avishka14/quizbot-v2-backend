package com.quizbotv2.Controller;

import com.quizbotv2.service.OpenRouterService;
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

    @PostMapping
    public String chat(@RequestBody String quizTopic){
        return openRouterService.getQuizResponse(quizTopic);
    }
}
