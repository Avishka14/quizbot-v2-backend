package com.quizbotv2.repo;

import com.quizbotv2.model.Quizzes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizzesRepository extends JpaRepository<Quizzes, Long> {
}
