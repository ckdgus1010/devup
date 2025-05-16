package com.upstage.devup.user.answer.repository;

import com.upstage.devup.global.entity.UserAnswerStat;
import com.upstage.devup.user.statistics.dto.CategoryCountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnswerStatRepository extends JpaRepository<UserAnswerStat, Long> {
    Optional<UserAnswerStat> findByUserIdAndQuestionId(Long userId, Long questionId);

    long countUserAnswerStatByUserId(Long userId);

    long countUserAnswerStatByUserIdAndCorrectCountGreaterThan(Long userId, int i);

    @Query(value = """
        SELECT
            c.id,
            c.category,
            c.color,
            COUNT(*)
        FROM user_answer_stats uas
        JOIN questions q
            ON uas.question_id = q.id
        JOIN categories c
            ON q.category_id = c.id
        WHERE uas.user_id = :userId
        GROUP BY c.id, c.category, c.color
    """, nativeQuery = true)
    List<CategoryCountDto> findCategoriesByUserId(Long userId);

    Page<UserAnswerStat> findByUserId(Long userId, Pageable pageable);
}
