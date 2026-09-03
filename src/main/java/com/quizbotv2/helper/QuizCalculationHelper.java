package com.quizbotv2.helper;

import com.quizbotv2.dto.userdtos.UserQuestionsDTO;
import com.quizbotv2.dto.userdtos.UserQuizInputsDTO;
import com.quizbotv2.dto.userdtos.UserQuizResultDTO;
import com.quizbotv2.exception.UserExceptions;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
public final class QuizCalculationHelper {

    private QuizCalculationHelper() {
        // Utility class
    }

    public static void validateQuizTime(UserQuizInputsDTO input) {

        if (input.startTime() == null || input.endTime() == null) {

            log.error(
                    "Quiz result calculation failed: missing start or end time. userId={}, quizId={}",
                    input.id(),
                    input.quizId()
            );

            throw new UserExceptions(
                    "Quiz start time and end time are required to calculate the result"
            );
        }

        if (input.endTime().isBefore(input.startTime())) {

            log.error(
                    "Quiz result calculation failed: endTime={} is before startTime={}. userId={}, quizId={}",
                    input.endTime(),
                    input.startTime(),
                    input.id(),
                    input.quizId()
            );

            throw new UserExceptions(
                    "Quiz end time cannot be before start time"
            );
        }
    }

    public static int calculateMark(List<UserQuestionsDTO> questions) {

        int mark = 0;

        for (UserQuestionsDTO question : questions) {

            if (Objects.equals(
                    question.correctAnswer(),
                    question.userAnswer()
            )) {
                mark++;
            }
        }

        return mark;
    }

    public static String calculateTotalTime(
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        Duration duration = Duration.between(
                startTime,
                endTime
        );

        long totalSeconds = duration.getSeconds();

        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                seconds
        );
    }

}