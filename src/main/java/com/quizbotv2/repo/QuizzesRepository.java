package com.quizbotv2.repo;

import com.quizbotv2.model.Quizzes;
import com.quizbotv2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizzesRepository extends JpaRepository<Quizzes, Long> {
    List<Quizzes> findByUserId(UUID userId);
}
