package com.quizbotv2.service;

import com.quizbotv2.dto.quizdtos.QuizGenerationRequest;
import com.quizbotv2.dto.quizdtos.QuizQuestion;
import com.quizbotv2.dto.quizdtos.QuizResult;
import com.quizbotv2.model.Question;
import com.quizbotv2.model.Quizzes;
import com.quizbotv2.model.User;
import com.quizbotv2.repo.QuestionRepository;
import com.quizbotv2.repo.QuizzesRepository;
import com.quizbotv2.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final OpenRouterService openRouterService;
    private final UserRepository userRepository;
    private final QuizzesRepository quizzesRepository;
    private final QuestionRepository questionRepository;

    public UserService(
            OpenRouterService openRouterService,
            UserRepository userRepository,
            QuizzesRepository quizzesRepository,
            QuestionRepository questionRepository
    ) {
        this.openRouterService = openRouterService;
        this.userRepository = userRepository;
        this.quizzesRepository = quizzesRepository;
        this.questionRepository = questionRepository;
    }



}

