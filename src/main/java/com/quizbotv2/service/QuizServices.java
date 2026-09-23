package com.quizbotv2.service;

import com.quizbotv2.dto.userdtos.UserQuizInputsDTO;
import com.quizbotv2.dto.userdtos.UserQuizResultDTO;
import com.quizbotv2.exception.UserExceptions;
import com.quizbotv2.helper.QuizCalculationHelper;
import com.quizbotv2.model.Marks;
import com.quizbotv2.model.Quizzes;
import com.quizbotv2.model.User;
import com.quizbotv2.repo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
public class QuizServices {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final QuizzesRepository quizzesRepository;
    private final UserRepository userRepository;
    private final MarkRepository markRepository;


    public QuizServices(
            AnswerRepository answerRepository,
            QuestionRepository questionRepository,
            QuizzesRepository quizzesRepository,
            UserRepository userRepository, MarkRepository markRepository
    ) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.quizzesRepository = quizzesRepository;
        this.userRepository = userRepository;
        this.markRepository = markRepository;
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





}

