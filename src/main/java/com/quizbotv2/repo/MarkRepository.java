package com.quizbotv2.repo;

import com.quizbotv2.model.Marks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MarkRepository extends JpaRepository<Marks, Long> {
    Optional<Marks> findByQuizzesId(Long quizId);
}
