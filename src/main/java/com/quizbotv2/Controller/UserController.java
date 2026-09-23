package com.quizbotv2.Controller;

import com.quizbotv2.dto.quizdtos.QuizGenerationRequest;
import com.quizbotv2.dto.quizdtos.QuizResult;
import com.quizbotv2.dto.userdtos.UserQuizInputsDTO;
import com.quizbotv2.dto.userdtos.UserQuizResultDTO;
import com.quizbotv2.helper.CustomOIDCAuthUser;
import com.quizbotv2.service.OpenRouterService;
import com.quizbotv2.service.QuizServices;
import com.quizbotv2.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final QuizServices quizServices;
    private final UserService userService;


    public UserController(QuizServices quizServices, UserService userService) {
        this.quizServices = quizServices;
        this.userService = userService;
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OidcUser principal){
        return "Welcome, " +principal.getFullName() + " " + principal.getEmail() + " ";
    }

    @PostMapping("/test")
    public QuizResult getQuiz(
            @RequestBody QuizGenerationRequest quizGenerationRequest,
            @RequestParam UUID userId
//            Authentication authentication
    ) {

//        CustomOIDCAuthUser principal = (CustomOIDCAuthUser) authentication.getPrincipal();

//        UUID userId = principal.getUser().getId();

        return userService.generateQuizWithUserId(quizGenerationRequest, userId);

    }

    @PostMapping("/calculate")
    public UserQuizResultDTO calculateResult(@RequestBody UserQuizInputsDTO quizInputsDTO){
        return quizServices.calculateResult(quizInputsDTO);
    }


}
