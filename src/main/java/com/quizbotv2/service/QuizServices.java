package com.quizbotv2.service;

import com.quizbotv2.dto.userdtos.UserQuizInputsDTO;
import com.quizbotv2.dto.userdtos.UserQuizResultDTO;
import com.quizbotv2.exception.UserExceptions;
import com.quizbotv2.helper.QuizCalculationHelper;
import com.quizbotv2.repo.AnswerRepository;
import com.quizbotv2.repo.QuestionRepository;
import com.quizbotv2.repo.QuizzesRepository;
import com.quizbotv2.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QuizServices {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final QuizzesRepository quizzesRepository;
    private final UserRepository userRepository;


    public QuizServices(
            AnswerRepository answerRepository,
            QuestionRepository questionRepository,
            QuizzesRepository quizzesRepository,
            UserRepository userRepository
    ) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.quizzesRepository = quizzesRepository;
        this.userRepository = userRepository;
    }

    public UserQuizResultDTO calculateResult(UserQuizInputsDTO input) {

        validateQuizInput(input);

        Long userId = input.id();
        Long quizId = input.quizId();

        int questionCount = input.questionsDTOS().size();

        int mark = QuizCalculationHelper.calculateMark(
                input.questionsDTOS()
        );

        String totalTime = QuizCalculationHelper.calculateTotalTime(
                input.startTime(),
                input.endTime()
        );

        log.info(
                "Quiz result calculated successfully: userId={}, quizId={}, mark={}, questionCount={}, totalTime={}",
                userId,
                quizId,
                mark,
                questionCount,
                totalTime
        );

        return new UserQuizResultDTO(
                userId,
                mark,
                totalTime,
                questionCount
        );
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



}

