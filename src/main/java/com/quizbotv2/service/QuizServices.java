package com.quizbotv2.service;

import com.quizbotv2.dto.quizdtos.QuizGenerationRequest;
import com.quizbotv2.dto.quizdtos.QuizQuestion;
import com.quizbotv2.dto.quizdtos.QuizResult;
import com.quizbotv2.dto.userdtos.UserQuizInputsDTO;
import com.quizbotv2.dto.userdtos.UserQuizResultDTO;
import com.quizbotv2.exception.UserExceptions;
import com.quizbotv2.helper.QuizCalculationHelper;
import com.quizbotv2.model.Marks;
import com.quizbotv2.model.Question;
import com.quizbotv2.model.Quizzes;
import com.quizbotv2.model.User;
import com.quizbotv2.repo.*;
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
public class QuizServices {


    private final QuestionRepository questionRepository;
    private final QuizzesRepository quizzesRepository;
    private final UserRepository userRepository;
    private final MarkRepository markRepository;
    private final OpenRouterService openRouterService;


    public QuizServices(
            QuestionRepository questionRepository,
            QuizzesRepository quizzesRepository,
            UserRepository userRepository, MarkRepository markRepository, OpenRouterService openRouterService
    ) {
        this.questionRepository = questionRepository;
        this.quizzesRepository = quizzesRepository;
        this.userRepository = userRepository;
        this.markRepository = markRepository;
        this.openRouterService = openRouterService;
    }

    public UserQuizResultDTO calculateResult(UserQuizInputsDTO input) {

        validateQuizInput(input);

        UUID userId = input.id();
        Long quizId = input.quizId();

        int questionCount = input.questionsDTOS().size();

        String mark = String.valueOf(QuizCalculationHelper.calculateMark(input.questionsDTOS()));

        String totalTime = QuizCalculationHelper.calculateTotalTime(
                input.startTime(),
                input.endTime()
        );

        UserQuizResultDTO quizResultDTO = new UserQuizResultDTO(
                userId,
                mark,
                totalTime,
                questionCount
        );

        saveMarksInDb(quizResultDTO, userId, quizId);
        return quizResultDTO;
    }

    private void validateQuizInput(UserQuizInputsDTO input) {

        if (input == null) {
            log.error("Quiz result calculation failed: input is null");

            throw new UserExceptions(
                    "Quiz input is required to calculate the result"
            );
        }

        if (input.id() == null || input.quizId() == null) {
            log.error(
                    "Quiz result calculation failed: userId={}, quizId={}",
                    input.id(),
                    input.quizId()
            );

            throw new UserExceptions(
                    "User ID and quiz ID are required to calculate"
            );
        }

        if (input.questionsDTOS() == null ||
                input.questionsDTOS().isEmpty()) {

            log.error(
                    "Quiz result calculation failed: no questions submitted. userId={}, quizId={}",
                    input.id(),
                    input.quizId()
            );

            throw new UserExceptions(
                    "At least one question is required to calculate the result"
            );
        }

        QuizCalculationHelper.validateQuizTime(input);
    }



    private void saveMarksInDb(UserQuizResultDTO resultDTO, UUID userId, Long quizId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found. userId={}", userId);
                    return new UserExceptions("User not found");
                });

        Quizzes quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> {
                    log.error("Quiz not found. quizId={}", quizId);
                    return new UserExceptions("Quiz not found");
                });

        Marks marks = new Marks();

        marks.setMark(resultDTO.getMark());
        marks.setUser(user);
        marks.setQuizzes(quiz);
        marks.setTotalTime(resultDTO.getTotalTime());
        marks.setQuestionCount(resultDTO.getQuestionCount());
        markRepository.save(marks);
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

