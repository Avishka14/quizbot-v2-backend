package com.quizbotv2.repo;

import com.quizbotv2.model.Answers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answers, Long> {
    @Query("SELECT a FROM Answers a JOIN FETCH a.question q WHERE q.quizzes.id = :quizId")
    List<Answers> findAnswersByQuizId(@Param("quizId") Long quizId);
}
