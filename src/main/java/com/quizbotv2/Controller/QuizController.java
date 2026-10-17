package com.quizbotv2.Controller;

import com.quizbotv2.dto.quizdtos.QuizGenerationRequest;
import com.quizbotv2.dto.quizdtos.QuizResult;
import com.quizbotv2.dto.userdtos.UserQuizInputsDTO;
import com.quizbotv2.dto.userdtos.UserQuizResultDTO;
import com.quizbotv2.helper.CustomOIDCAuthUser;
import com.quizbotv2.model.User;
import com.quizbotv2.service.OpenRouterService;
import com.quizbotv2.service.QuizServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/quiz")
public class QuizController {

    private final OpenRouterService openRouterService;
    private final QuizServices quizServices;

    public QuizController(OpenRouterService openRouterService, QuizServices quizServices) {
        this.openRouterService = openRouterService;
        this.quizServices = quizServices;
    }

    @PostMapping("/generate")
    public ResponseEntity<QuizResult> generateQuiz(@RequestBody QuizGenerationRequest request) {
        QuizResult result = openRouterService.generateQuiz(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/getquiz")
    public QuizResult getQuiz(
            @RequestBody QuizGenerationRequest quizGenerationRequest,
            @AuthenticationPrincipal CustomOIDCAuthUser principle
    ) {

        if(principle == null){
           return openRouterService.generateQuiz(quizGenerationRequest);
        }else{
            User user = principle.getUser();
           return quizServices.generateQuizWithUserId(quizGenerationRequest, user.getId());
        }

    }


    @PostMapping("/calculate")
    public UserQuizResultDTO calculateResult(@RequestBody UserQuizInputsDTO quizInputsDTO){
        return quizServices.calculateResult(quizInputsDTO);
    }



}
