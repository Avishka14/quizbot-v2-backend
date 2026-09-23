package com.quizbotv2.repo;

import com.quizbotv2.model.Marks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkRepository extends JpaRepository<Marks, Long> {
}
