package com.upstage.devup.question.repository;

import com.upstage.devup.question.domain.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Question, Long> {

    Page<Question> findAll(Pageable pageable);

    Page<Question> findByTitleContainsIgnoreCase(String title, Pageable pageable);
}
