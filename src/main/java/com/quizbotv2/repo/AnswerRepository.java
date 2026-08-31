package com.quizbotv2.repo;

import com.quizbotv2.model.Answers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answers, Long> {
}
