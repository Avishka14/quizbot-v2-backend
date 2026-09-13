package com.quizbotv2.service;

import com.quizbotv2.repo.QuestionRepository;
import com.quizbotv2.repo.QuizzesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DbService {

    private final QuizzesRepository quizzesRepository;
    private final QuestionRepository questionRepository;

    public DbService(QuizzesRepository quizzesRepository, QuestionRepository questionRepository) {
        this.quizzesRepository = quizzesRepository;
        this.questionRepository = questionRepository;
    }

//    public boolean saveQuizez(){
//
//
//
//    }


}
