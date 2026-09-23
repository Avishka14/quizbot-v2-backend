package com.quizbotv2.dto.userdtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserQuizInputsDTO(
        UUID id,
        Long quizId,
        List<UserQuestionsDTO> questionsDTOS,
        LocalDateTime startTime,
        LocalDateTime endTime

) {

}
