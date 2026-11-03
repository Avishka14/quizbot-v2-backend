package com.quizbotv2.Controller;

import com.quizbotv2.dto.userdtos.UserQuizzesHistoryDTO;
import com.quizbotv2.helper.CustomOIDCAuthUser;
import com.quizbotv2.model.User;
import com.quizbotv2.service.OpenRouterService;
import com.quizbotv2.service.QuizHistoryServices;
import com.quizbotv2.service.UserQuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final QuizHistoryServices quizHistoryServices;
    private final UserQuizService userQuizService;
    private final OpenRouterService openRouterService;


    public UserController(QuizHistoryServices quizHistoryServices, UserQuizService userQuizService, OpenRouterService openRouterService) {
        this.quizHistoryServices = quizHistoryServices;
        this.userQuizService = userQuizService;
        this.openRouterService = openRouterService;
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OidcUser principal){
        return "Welcome, " +principal.getFullName() + " " + principal.getEmail() + " ";
    }


    @GetMapping("/user")
    public ResponseEntity<?> userData(@AuthenticationPrincipal CustomOIDCAuthUser principle){
        if(principle == null){
            return ResponseEntity.status(401).build();
        }

        User user = principle.getUser();

        return ResponseEntity.ok(Map.of(
                "name", user.getName(),
                "email", user.getEmail(),
                "pictureUrl", user.getPictureUrl()
        ));
    }


}
