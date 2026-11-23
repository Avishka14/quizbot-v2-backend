package com.quizbot.service;


import com.quizbot.dto.userdtos.UserQuizAnswersDTO;
import com.quizbot.dto.userdtos.UserQuizzesHistoryDTO;
import com.quizbot.exception.ErrorCode;
import com.quizbot.exception.ResourceNotFoundException;
import com.quizbot.model.Answers;
import com.quizbot.model.Marks;
import com.quizbot.model.Quizzes;
import com.quizbot.model.User;
import com.quizbot.repo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class QuizHistoryServices {

    private final UserRepository userRepository;
    private final QuizzesRepository quizzesRepository;
    private final QuestionRepository questionRepository;
    private final MarkRepository markRepository;
    private final AnswerRepository answerRepository;


    public QuizHistoryServices(UserRepository userRepository, QuizzesRepository quizzesRepository, QuestionRepository questionRepository, MarkRepository markRepository, AnswerRepository answerRepository) {
        this.userRepository = userRepository;
        this.quizzesRepository = quizzesRepository;
        this.questionRepository = questionRepository;
        this.markRepository = markRepository;
        this.answerRepository = answerRepository;
    }

    @Transactional(readOnly = true)
    public List<UserQuizzesHistoryDTO> getUserQuizHistory(UUID userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Quiz history retrieval failed - user not found: {}", userId);
                    return new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND);
                });

        List<Quizzes> quizzesList = quizzesRepository.findByUserId(userId);

        if(quizzesList.isEmpty()){
            log.info("Not found any quizzes for user: {}", userId);
            throw new ResourceNotFoundException(ErrorCode.QUIZ_NOT_FOUND);
        }else{

            List<UserQuizzesHistoryDTO> historyDTOList = new ArrayList<>();

            for(Quizzes quiz: quizzesList){

                // 1. Marks for this quiz
                Marks marks = markRepository
                        .findByUserIdAndQuizzesId(userId, quiz.getId())
                        .orElseThrow(() -> {
                            log.error("Marks not found for user {} quiz {}", userId, quiz.getId());
                            return new ResourceNotFoundException(ErrorCode.QUIZ_MARKS_NOTFOUND);
                        });



                // 2. Questions + correct answers for this quiz
                List<UserQuizAnswersDTO> answersDTOS = new ArrayList<>();
                for(Answers answers: answerRepository.findAnswersByQuizId(quiz.getId())){
                    answersDTOS.add(new UserQuizAnswersDTO(
                            answers.getId(),
                            answers.getQuestion().getQuestionTitle(),
                            answers.getCorrectAnswer()
                    ));
                }

                // 3. Build the DTO for this quiz
                historyDTOList.add(new UserQuizzesHistoryDTO(
                        quiz.getId(),
                        quiz.getCreatedAt(),
                        marks.getMark(),
                        marks.getTotalTime(),
                        answersDTOS
                ));

            }

            return historyDTOList;
        }
    }



}

