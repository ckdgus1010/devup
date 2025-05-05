package com.upstage.devup.answer.repository;

import com.upstage.devup.answer.domain.entity.UserAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    @Query(value = """
            SELECT *
            FROM (
                SELECT ua.*
                FROM user_answers ua
                JOIN (
                    SELECT question_id, MAX(created_at) AS latest_created_at
                    FROM user_answers
                    WHERE user_id = :userId
                    GROUP BY question_id
                ) temp
                ON ua.question_id = temp.question_id
                   AND ua.created_at = temp.latest_created_at
                WHERE ua.user_id = :userId
            ) sub
            ORDER BY sub.created_at DESC
            """, countQuery = """
            SELECT COUNT(*)
            FROM (
                SELECT MAX(created_at)
                FROM user_answers
                WHERE user_id :userId
                GROUP BY question_id
            ) count_sub
            """, nativeQuery = true)
    Page<UserAnswer> findLatestAnswersByUserId(Long userId, Pageable pageable);

    Optional<UserAnswer> findTopByUserIdAndQuestionIdOrderByCreatedAtDesc(Long userId, Long questionId);
}
