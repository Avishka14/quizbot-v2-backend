package com.quizbotv2.Controller;

import com.quizbotv2.dto.quizdtos.QuizGenerationRequest;
import com.quizbotv2.dto.quizdtos.QuizResult;
import com.quizbotv2.dto.userdtos.UserQuizInputsDTO;
import com.quizbotv2.dto.userdtos.UserQuizResultDTO;
import com.quizbotv2.dto.userdtos.UserQuizzesHistoryDTO;
import com.quizbotv2.helper.CustomOIDCAuthUser;
import com.quizbotv2.model.User;
import com.quizbotv2.service.OpenRouterService;
import com.quizbotv2.service.QuizHistoryServices;
import com.quizbotv2.service.UserQuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/quiz")
public class QuizController {

    private final OpenRouterService openRouterService;
    private final QuizHistoryServices quizHistoryServices;
    private final UserQuizService userQuizService;

    public QuizController(OpenRouterService openRouterService, QuizHistoryServices quizHistoryServices, UserQuizService userQuizService) {
        this.openRouterService = openRouterService;
        this.quizHistoryServices = quizHistoryServices;
        this.userQuizService = userQuizService;
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
           return userQuizService.generateQuizWithUserId(quizGenerationRequest, user.getId());
        }

    }


    @PostMapping("/getmarks")
    public ResponseEntity<?> calculateResult(
            @RequestBody UserQuizInputsDTO quizInputsDTO,
            @AuthenticationPrincipal CustomOIDCAuthUser principle){

        if(principle == null ){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }else{
            User user = principle.getUser();
            LocalDateTime submitDateTime = LocalDateTime.now();

            UserQuizInputsDTO userQuizInputsDTO = new UserQuizInputsDTO(
                    user.getId(),
                    quizInputsDTO.quizId(),
                    quizInputsDTO.questionsDTOS(),
                    submitDateTime
            );

            UserQuizResultDTO userQuizResultDTO =  userQuizService.calculateResult(userQuizInputsDTO);
            return ResponseEntity.ok(userQuizResultDTO);
        }

    }


    @GetMapping("/quiz-history")
    public ResponseEntity<List<UserQuizzesHistoryDTO>> getUserQuizHistory(
            @AuthenticationPrincipal CustomOIDCAuthUser principle) {

        if(principle == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }else{
            User user = principle.getUser();
            return ResponseEntity.ok(quizHistoryServices.getUserQuizHistory(user.getId()));
        }

    }



}
