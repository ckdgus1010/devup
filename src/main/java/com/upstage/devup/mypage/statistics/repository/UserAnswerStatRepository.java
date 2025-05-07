package com.upstage.devup.mypage.statistics.repository;

import com.upstage.devup.mypage.statistics.domain.entity.UserAnswerStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAnswerStatRepository extends JpaRepository<UserAnswerStat, Long> {
    long countUserAnswerStatByUserId(Long userId);

    long countUserAnswerStatByUserIdAndCorrectCountGreaterThan(Long userId, int correctCountIsGreaterThan);
}
