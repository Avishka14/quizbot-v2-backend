package com.quizbotv2.service;

import com.quizbotv2.dto.userdtos.UserQuestionsDTO;
import com.quizbotv2.dto.userdtos.UserQuizInputsDTO;
import com.quizbotv2.dto.userdtos.UserQuizResultDTO;
import com.quizbotv2.exception.UserExceptions;
import com.quizbotv2.repo.AnswerRepository;
import com.quizbotv2.repo.QuestionRepository;
import com.quizbotv2.repo.QuizzesRepository;
import com.quizbotv2.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

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

    public UserQuizResultDTO calculateResult(UserQuizInputsDTO userQuizInputsDTO) {

        log.info("Starting quiz result calculation");

        if (userQuizInputsDTO == null) {
            log.error("Quiz result calculation failed: input is null");

            throw new UserExceptions(
                    "Quiz input is required to calculate the result"
            );
        }

        if (userQuizInputsDTO.id() == null ||
                userQuizInputsDTO.quizId() == null) {

            log.error(
                    "Quiz result calculation failed: userId={}, quizId={}",
                    userQuizInputsDTO.id(),
                    userQuizInputsDTO.quizId()
            );

            throw new UserExceptions(
                    "User ID and quiz ID are required to calculate"
            );
        }

        Long userId = userQuizInputsDTO.id();
        Long quizId = userQuizInputsDTO.quizId();

        log.debug(
                "Valid quiz result request received: userId={}, quizId={}",
                userId,
                quizId
        );

        if (userQuizInputsDTO.questionsDTOS() == null ||
                userQuizInputsDTO.questionsDTOS().isEmpty()) {

            log.error(
                    "Quiz result calculation failed: no questions submitted. userId={}, quizId={}",
                    userId,
                    quizId
            );

            throw new UserExceptions(
                    "At least one question is required to calculate the result"
            );
        }

        if (userQuizInputsDTO.startTime() == null ||
                userQuizInputsDTO.endTime() == null) {

            log.error(
                    "Quiz result calculation failed: missing start or end time. userId={}, quizId={}",
                    userId,
                    quizId
            );

            throw new UserExceptions(
                    "Quiz start time and end time are required to calculate the result"
            );
        }

        if (userQuizInputsDTO.endTime()
                .isBefore(userQuizInputsDTO.startTime())) {

            log.error(
                    "Quiz result calculation failed: endTime={} is before startTime={}. userId={}, quizId={}",
                    userQuizInputsDTO.endTime(),
                    userQuizInputsDTO.startTime(),
                    userId,
                    quizId
            );

            throw new UserExceptions(
                    "Quiz end time cannot be before start time"
            );
        }

        int questionCount = userQuizInputsDTO.questionsDTOS().size();

        log.debug(
                "Question count calculated: userId={}, quizId={}, questionCount={}",
                userId,
                quizId,
                questionCount
        );

        int mark = 0;

        for (UserQuestionsDTO question : userQuizInputsDTO.questionsDTOS()) {

            if (Objects.equals(
                    question.correctAnswer(),
                    question.userAnswer()
            )) {
                mark++;
            }
        }

        log.debug(
                "Quiz mark calculated: userId={}, quizId={}, mark={}, questionCount={}",
                userId,
                quizId,
                mark,
                questionCount
        );

        Duration duration = Duration.between(
                userQuizInputsDTO.startTime(),
                userQuizInputsDTO.endTime()
        );

        long totalSeconds = duration.getSeconds();

        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        String totalTime = String.format(
                "%02d:%02d",
                minutes,
                seconds
        );

        log.debug(
                "Quiz duration calculated: userId={}, quizId={}, totalTime={}",
                userId,
                quizId,
                totalTime
        );


        UserQuizResultDTO result = new UserQuizResultDTO(
                userId,
                mark,
                totalTime,
                questionCount
        );

        log.info(
                "Quiz result calculated successfully: userId={}, quizId={}, mark={}, questionCount={}, totalTime={}",
                userId,
                quizId,
                mark,
                questionCount,
                totalTime
        );

        return result;
    }
}

