package com.quizbot.Controller;


import com.quizbot.helper.CustomOIDCAuthUser;
import com.quizbot.model.User;
import com.quizbot.service.OpenRouterService;
import com.quizbot.service.QuizHistoryServices;
import com.quizbot.service.UserQuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

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

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> userData(@AuthenticationPrincipal CustomOIDCAuthUser principle){
        if(principle == null){
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        User user = principle.getUser();

        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "name", user.getName(),
                "email", user.getEmail(),
                "pictureUrl", user.getPictureUrl()
        ));
    }


}
