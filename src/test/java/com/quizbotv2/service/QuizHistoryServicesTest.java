package com.quizbotv2.service;

import com.quizbotv2.dto.userdtos.UserQuizzesHistoryDTO;
import com.quizbotv2.exception.ResourceNotFoundException;
import com.quizbotv2.model.Answers;
import com.quizbotv2.model.Marks;
import com.quizbotv2.model.Question;
import com.quizbotv2.model.Quizzes;
import com.quizbotv2.model.User;
import com.quizbotv2.repo.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizHistoryServicesTest {

    @Mock private UserRepository userRepository;
    @Mock private QuizzesRepository quizzesRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private MarkRepository markRepository;
    @Mock private AnswerRepository answerRepository;

    @InjectMocks
    private QuizHistoryServices service;

    private final UUID userId = UUID.randomUUID();

    @Test
    void getUserQuizHistory_throws_whenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getUserQuizHistory(userId));
    }

    @Test
    void getUserQuizHistory_throws_whenUserHasNoQuizzes() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(quizzesRepository.findByUserId(userId)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> service.getUserQuizHistory(userId));
    }

    @Test
    void getUserQuizHistory_throws_whenMarksMissing(@Mock Quizzes quiz) {
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(quizzesRepository.findByUserId(userId)).thenReturn(List.of(quiz));
        when(markRepository.findByUserIdAndQuizzesId(eq(userId), any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getUserQuizHistory(userId));
    }

    @Test
    void getUserQuizHistory_returnsHistory_whenDataExists(
            @Mock Quizzes quiz, @Mock Marks marks, @Mock Answers answer, @Mock Question question) {

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(quizzesRepository.findByUserId(userId)).thenReturn(List.of(quiz));
        when(markRepository.findByUserIdAndQuizzesId(eq(userId), any())).thenReturn(Optional.of(marks));
        when(answerRepository.findAnswersByQuizId(any())).thenReturn(List.of(answer));
        when(answer.getQuestion()).thenReturn(question);

        List<UserQuizzesHistoryDTO> result = service.getUserQuizHistory(userId);

        assertEquals(1, result.size());
    }
}