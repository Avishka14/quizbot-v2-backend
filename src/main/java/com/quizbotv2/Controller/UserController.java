package com.quizbotv2.Controller;

import com.quizbotv2.dto.quizdtos.QuizGenerationRequest;
import com.quizbotv2.dto.quizdtos.QuizResult;
import com.quizbotv2.dto.userdtos.UserDTO;
import com.quizbotv2.dto.userdtos.UserQuizInputsDTO;
import com.quizbotv2.dto.userdtos.UserQuizResultDTO;
import com.quizbotv2.helper.CustomOIDCAuthUser;
import com.quizbotv2.model.User;
import com.quizbotv2.service.OpenRouterService;
import com.quizbotv2.service.QuizServices;
import com.quizbotv2.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final QuizServices quizServices;
    private final UserService userService;
    private final OpenRouterService openRouterService;


    public UserController(QuizServices quizServices, UserService userService, OpenRouterService openRouterService) {
        this.quizServices = quizServices;
        this.userService = userService;
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
