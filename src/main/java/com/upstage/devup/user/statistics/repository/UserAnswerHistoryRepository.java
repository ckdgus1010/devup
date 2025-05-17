package com.upstage.devup.user.statistics.repository;

import com.upstage.devup.global.entity.UserAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAnswerHistoryRepository extends JpaRepository<UserAnswer, Long> {
    Page<UserAnswer> findByUserId(Long userId, Pageable pageable);
}
