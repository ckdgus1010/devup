package com.upstage.devup.question.repository;

import com.upstage.devup.question.domain.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Question, Long> {

    Page<Question> findAll(Pageable pageable);
}
