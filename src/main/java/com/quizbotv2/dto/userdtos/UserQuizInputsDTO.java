package com.quizbotv2.dto.userdtos;

import java.time.LocalDateTime;
import java.util.List;

public record UserQuizInputsDTO(
        Long id,
        Long quizId,
        List<UserQuestionsDTO> questionsDTOS,
        LocalDateTime startTime,
        LocalDateTime endTime

) {

}
