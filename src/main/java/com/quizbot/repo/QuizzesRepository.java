package com.quizbot.repo;

import com.quizbot.model.Quizzes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizzesRepository extends JpaRepository<Quizzes, Long> {
    List<Quizzes> findByUserId(UUID userId);
}
