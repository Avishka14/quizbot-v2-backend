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


//    Generates a quiz for a logged-in user and saves
    @Transactional
    public QuizResult generateQuizWithUserId(
            QuizGenerationRequest quizGenerationRequest,
            UUID userId
    ) {

        Optional<User> optUser = userRepository.findById(userId);

        if (optUser.isEmpty()) {
            log.error("Quiz generation failed - user not found: {}", userId);
            throw new RuntimeException("User not found: " + userId);
        }

        User user = optUser.get();

        QuizResult quizResult = openRouterService.generateQuiz(quizGenerationRequest);

        if (quizResult == null) {
            log.error("Quiz generation returned null result for user: {}", userId);
            throw new RuntimeException("Quiz generation returned an empty result");
        }

        List<QuizQuestion> generatedQuestions = quizResult.getQuestions();

        Quizzes quiz = new Quizzes();

        quiz.setUser(user);
        quiz.setCreatedAt(LocalDateTime.now());


        Quizzes savedQuiz = quizzesRepository.save(quiz);

        saveQuestions(generatedQuestions, savedQuiz);

        return quizResult;
    }



    //save questions each in db
    private void saveQuestions(List<QuizQuestion> generatedQuestions, Quizzes quiz ) {

        List<Question> questions = new ArrayList<>();

        for (QuizQuestion quizQuestion : generatedQuestions) {

            if (quizQuestion == null ||
                    quizQuestion.getQuestion() == null ||
                    quizQuestion.getQuestion().isBlank()) {

                continue;
            }

            Question question = new Question();

            question.setQuestionTitle( quizQuestion.getQuestion() );

            question.setQuizzes(quiz);
            questions.add(question);
        }

        if (questions.isEmpty()) {
            log.error("No valid questions available to save for Quiz ID: {}", quiz.getId() );
            throw new RuntimeException("No valid questions available to save" );
        }

        try {

            questionRepository.saveAll(questions);

        } catch (Exception e) {

            log.error("Failed to save questions for Quiz ID: {}", quiz.getId(), e );
            throw new RuntimeException("Failed to save quiz questions", e);
        }
    }
}

