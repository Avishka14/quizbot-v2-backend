package com.quizbot.dto.userdtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserQuizInputsDTO(
        UUID id,
        Long quizId,
        List<UserQuestionsDTO> questionsDTOS,
        LocalDateTime endTime

) {

}
