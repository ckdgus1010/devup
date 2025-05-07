package com.upstage.devup.user.answer.repository;

import com.upstage.devup.user.answer.domain.entity.UserAnswerStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAnswerStatRepository extends JpaRepository<UserAnswerStat, Long> {
    Optional<UserAnswerStat> findByUserIdAndQuestionId(Long userId, Long questionId);

    long countUserAnswerStatByUserId(Long userId);

    long countUserAnswerStatByUserIdAndCorrectCountGreaterThan(Long userId, int i);
}
