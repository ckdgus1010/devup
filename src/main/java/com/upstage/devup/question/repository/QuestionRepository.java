package com.upstage.devup.question.repository;

import com.upstage.devup.global.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findAll(Pageable pageable);

    @Query(
            value = """
                SELECT *
                FROM questions q
                WHERE UPPER(q.title) LIKE UPPER(CONCAT('%', :title, '%'))
                ORDER BY q.id DESC
            """, countQuery = """
                SELECT COUNT(*)
                FROM questions q
                WHERE UPPER(q.title) LIKE UPPER(CONCAT('%', :title, '%'))
            """, nativeQuery = true
    )
    Page<Question> findByTitleContainsIgnoreCase(String title, Pageable pageable);
}
