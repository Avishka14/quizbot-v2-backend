package com.quizbot.service;


import com.quizbot.dto.quizdtos.QuizGenerationRequest;
import com.quizbot.dto.quizdtos.QuizQuestion;
import com.quizbot.dto.quizdtos.QuizResult;
import com.quizbot.dto.userdtos.UserQuizInputsDTO;
import com.quizbot.dto.userdtos.UserQuizResultDTO;
import com.quizbot.exception.ErrorCode;
import com.quizbot.exception.QuizGenerationException;
import com.quizbot.exception.ResourceNotFoundException;
import com.quizbot.exception.UserExceptions;
import com.quizbot.helper.QuizCalculationHelper;
import com.quizbot.model.*;
import com.quizbot.repo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserQuizService {

    private final OpenRouterService openRouterService;
    private final UserRepository userRepository;
    private final QuizzesRepository quizzesRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final MarkRepository markRepository;

    public UserQuizService(
            OpenRouterService openRouterService,
            UserRepository userRepository,
            QuizzesRepository quizzesRepository,
            QuestionRepository questionRepository, AnswerRepository answerRepository, MarkRepository markRepository
    ) {
        this.openRouterService = openRouterService;
        this.userRepository = userRepository;
        this.quizzesRepository = quizzesRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.markRepository = markRepository;
    }

    //    Generates a quiz for a logged-in user and saves
    @Transactional
    public QuizResult generateQuizWithUserId(QuizGenerationRequest request, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Quiz generation failed - user not found: {}", userId);
                    return new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND);
                });

        QuizResult quizResult = openRouterService.generateQuiz(request);

        if (quizResult == null) {
            log.error("Quiz generation returned null result for user: {}", userId);
            throw new QuizGenerationException(ErrorCode.QUIZ_EMPTY_RESULT);
        }

        Quizzes quiz = new Quizzes();
        quiz.setId(quizResult.id());
        quiz.setUser(user);
        quiz.setCreatedAt(LocalDateTime.now());

        Quizzes savedQuiz = quizzesRepository.save(quiz);

        saveQuestionsInDb(quizResult.questions(), savedQuiz);
        return quizResult;
    }

    //save questions each in db
    private void saveQuestionsInDb(List<QuizQuestion> generatedQuestions, Quizzes quiz ) {

        List<Question> questions = new ArrayList<>();
        List<Answers> answersList = new ArrayList<>();

        for (QuizQuestion quizQuestion : generatedQuestions) {

            if (quizQuestion == null ||
                    quizQuestion.question() == null ||
                    quizQuestion.question().isBlank() ||
                    quizQuestion.options() == null ||
                    quizQuestion.correctAnswer() == null){

                continue;
            }

            Question question = new Question();

            question.setQuestionTitle( quizQuestion.question());
            question.setQuizzes(quiz);
            questions.add(question);

            Answers answers = new Answers();
            answers.setQuestion(question);
            answers.setCorrectAnswer(quizQuestion.correctAnswer());
            answersList.add(answers);

        }

        if (questions.isEmpty()) {
            log.error("No valid questions available to save for Quiz ID: {}", quiz.getId());
            throw new QuizGenerationException(ErrorCode.QUIZ_NO_VALID_QUESTIONS);
        }

        try {

            questionRepository.saveAll(questions);

            //save answers in db
            saveAnswersInDB(answersList, quiz);

        } catch (DataAccessException e) {
            log.error("Failed to save quiz questions. quizId={}", quiz.getId(), e);
            throw new QuizGenerationException(ErrorCode.QUIZ_SAVE_FAILED, e);
        }
    }

    // save all answers in database
    private void saveAnswersInDB(List<Answers> answers, Quizzes quiz){

        if (answers.isEmpty()) {
            log.error("No valid answers available to save for Quiz ID: {}", quiz.getId());
            throw new QuizGenerationException(ErrorCode.QUIZ_NO_VALID_QUESTIONS);
        }

        try{
            answerRepository.saveAll(answers);

        } catch (DataAccessException e) {
            log.error("Failed to save answers. quizId={}", quiz.getId(), e);
            throw new QuizGenerationException(ErrorCode.QUIZ_SAVE_FAILED, e);
        }

    }

    @Transactional
    public UserQuizResultDTO calculateResult(UserQuizInputsDTO input) {

        UUID userId = input.id();
        Long quizId = input.quizId();

        Optional<Quizzes> quizzesOptional = quizzesRepository.findById(quizId);

        if(quizzesOptional.isPresent()){

            Quizzes quizzes = quizzesOptional.get();
            LocalDateTime startDateTime = quizzes.getCreatedAt();

            validateQuizInput(input, startDateTime);

            int questionCount = input.questionsDTOS().size();

            String mark = String.valueOf(QuizCalculationHelper.calculateMark(input.questionsDTOS()));

            String totalTime = QuizCalculationHelper.calculateTotalTime(
                    startDateTime,
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

        }else{
            log.error("Quiz result calculation failed: Quiz ID is invalid ID: {}", quizId);
            throw new ResourceNotFoundException(ErrorCode.QUIZ_NOT_FOUND);
        }

    }

    private void validateQuizInput(UserQuizInputsDTO input, LocalDateTime startTime) {

        if (input == null) {
            log.error("Quiz result calculation failed: input is null");
            throw new UserExceptions(ErrorCode.QUIZ_INPUT_REQUIRED);
        }

        if (input.id() == null || input.quizId() == null) {
            log.error(
                    "Quiz result calculation failed: userId={}, quizId={}",
                    input.id(),
                    input.quizId()
            );

            throw new UserExceptions(ErrorCode.QUIZ_IDS_REQUIRED);
        }

        if (input.questionsDTOS() == null ||
                input.questionsDTOS().isEmpty()) {

            log.error(
                    "Quiz result calculation failed: no questions submitted. userId={}, quizId={}",
                    input.id(),
                    input.quizId()
            );

            throw new UserExceptions(ErrorCode.QUIZ_NO_QUESTIONS_SUBMITTED);
        }

        QuizCalculationHelper.validateQuizTime(input, startTime);
    }



    private void saveMarksInDb(UserQuizResultDTO resultDTO, UUID userId, Long quizId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found. userId={}", userId);
                    return new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND);
                });

        Quizzes quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> {
                    log.error("Quiz not found. quizId={}", quizId);
                    return new ResourceNotFoundException(ErrorCode.QUIZ_NOT_FOUND);
                });

        Marks marks = markRepository
                .findByQuizzesId(quizId)
                .orElseGet(Marks::new);

        marks.setMark(resultDTO.mark());
        marks.setUser(user);
        marks.setQuizzes(quiz);
        marks.setTotalTime(resultDTO.totalTime());
        marks.setQuestionCount(resultDTO.questionCount());
        markRepository.save(marks);
    }




}

