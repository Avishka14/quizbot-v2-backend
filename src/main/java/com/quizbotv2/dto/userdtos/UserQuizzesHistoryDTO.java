package com.quizbotv2.dto.userdtos;

import java.time.LocalDateTime;
import java.util.List;

public record UserQuizzesHistoryDTO(
        Long id,
        LocalDateTime createdDate,
        String marks,
        String takenTime,
        List<UserQuizAnswersDTO> questionAnswerList
) {
}
