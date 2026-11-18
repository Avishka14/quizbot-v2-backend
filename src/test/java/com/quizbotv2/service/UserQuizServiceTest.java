package com.quizbotv2.service;

import com.quizbotv2.dto.quizdtos.QuizGenerationRequest;
import com.quizbotv2.dto.quizdtos.QuizQuestion;
import com.quizbotv2.dto.quizdtos.QuizResult;
import com.quizbotv2.dto.userdtos.UserQuizInputsDTO;
import com.quizbotv2.exception.QuizGenerationException;
import com.quizbotv2.exception.ResourceNotFoundException;
import com.quizbotv2.exception.UserExceptions;
import com.quizbotv2.model.Question;
import com.quizbotv2.model.Quizzes;
import com.quizbotv2.model.User;
import com.quizbotv2.repo.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserQuizServiceTest {

    @Mock private OpenRouterService openRouterService;
    @Mock private UserRepository userRepository;
    @Mock private QuizzesRepository quizzesRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private AnswerRepository answerRepository;
    @Mock private MarkRepository markRepository;

    @InjectMocks
    private UserQuizService service;

    private final UUID userId = UUID.randomUUID();

    // ---------- generateQuizWithUserId ----------

    @Test
    void generateQuiz_throws_whenUserNotFound(@Mock QuizGenerationRequest request) {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.generateQuizWithUserId(request, userId));

        verifyNoInteractions(openRouterService);
    }

    @Test
    void generateQuiz_throws_whenResultIsNull(@Mock QuizGenerationRequest request) {
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(openRouterService.generateQuiz(request)).thenReturn(null);

        assertThrows(QuizGenerationException.class,
                () -> service.generateQuizWithUserId(request, userId));

        verifyNoInteractions(quizzesRepository);
    }

    @Test
    void generateQuiz_throws_whenNoValidQuestions(
            @Mock QuizGenerationRequest request, @Mock QuizResult quizResult) {

        QuizQuestion invalid = mock(QuizQuestion.class); // question() returns null -> skipped

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(openRouterService.generateQuiz(request)).thenReturn(quizResult);
        when(quizResult.questions()).thenReturn(List.of(invalid));
        when(quizzesRepository.save(any(Quizzes.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThrows(QuizGenerationException.class,
                () -> service.generateQuizWithUserId(request, userId));

        verifyNoInteractions(questionRepository, answerRepository);
    }

    @Test
    void generateQuiz_savesQuestionsAndAnswers_whenValid(
            @Mock QuizGenerationRequest request, @Mock QuizResult quizResult, @Mock QuizQuestion validQuestion) {

        when(validQuestion.question()).thenReturn("What is Java?");
        when(validQuestion.correctAnswer()).thenReturn("A language");

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(openRouterService.generateQuiz(request)).thenReturn(quizResult);
        when(quizResult.questions()).thenReturn(List.of(validQuestion));
        when(quizzesRepository.save(any(Quizzes.class))).thenAnswer(inv -> inv.getArgument(0));

        QuizResult result = service.generateQuizWithUserId(request, userId);

        assertSame(quizResult, result);
        verify(questionRepository).saveAll(anyList());
        verify(answerRepository).saveAll(anyList());
    }

    @Test
    void generateQuiz_throws_whenSavingQuestionsFails(
            @Mock QuizGenerationRequest request, @Mock QuizResult quizResult, @Mock QuizQuestion validQuestion) {

        when(validQuestion.question()).thenReturn("What is Java?");
        when(validQuestion.correctAnswer()).thenReturn("A language");

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(openRouterService.generateQuiz(request)).thenReturn(quizResult);
        when(quizResult.questions()).thenReturn(List.of(validQuestion));
        when(quizzesRepository.save(any(Quizzes.class))).thenAnswer(inv -> inv.getArgument(0));
        when(questionRepository.saveAll(anyList()))
                .thenThrow(new DataAccessResourceFailureException("db down"));

        assertThrows(QuizGenerationException.class,
                () -> service.generateQuizWithUserId(request, userId));

        verifyNoInteractions(answerRepository);
    }

    // ---------- calculateResult ----------

    @Test
    void calculateResult_throws_whenQuizNotFound(@Mock UserQuizInputsDTO input) {
        when(input.quizId()).thenReturn(1L);
        when(quizzesRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.calculateResult(input));
    }

    @Test
    void calculateResult_throws_whenNoQuestionsSubmitted(
            @Mock UserQuizInputsDTO input, @Mock Quizzes quiz) {

        when(input.id()).thenReturn(userId);
        when(input.quizId()).thenReturn(1L);
        when(input.questionsDTOS()).thenReturn(List.of());
        when(quizzesRepository.findById(1L)).thenReturn(Optional.of(quiz));

        assertThrows(UserExceptions.class, () -> service.calculateResult(input));

        verifyNoInteractions(markRepository);
    }
}