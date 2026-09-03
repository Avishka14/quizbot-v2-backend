package com.quizbotv2.Controller;

import com.quizbotv2.dto.quizdtos.QuizGenerationRequest;
import com.quizbotv2.dto.quizdtos.QuizResponse;
import com.quizbotv2.dto.userdtos.UserQuizInputsDTO;
import com.quizbotv2.dto.userdtos.UserQuizResultDTO;
import com.quizbotv2.service.QuizServices;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final QuizServices quizServices;

    public UserController(QuizServices quizServices) {
        this.quizServices = quizServices;
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OidcUser principal){
        return "Welcome, " +principal.getFullName() + " " + principal.getEmail() + " ";
    }

    @PostMapping("/quizreq")
    public void getQuiz(@RequestBody QuizGenerationRequest request){

        System.out.println("Topic" + request.topic());
        System.out.println("Difficult" + request.difficulty());
        System.out.println("Count" + request.questionCount());

    }

    @PostMapping("/calculate")
    public UserQuizResultDTO calculateResult(@RequestBody UserQuizInputsDTO quizInputsDTO){

        return quizServices.calculateResult(quizInputsDTO);

    }


}
