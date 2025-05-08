package com.upstage.devup.user.answer.repository;

import com.upstage.devup.user.answer.domain.entity.UserWrongAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserWrongAnswerRepository extends JpaRepository<UserWrongAnswer, Long> {
    Optional<UserWrongAnswer> findTopByUserIdAndQuestionIdOrderByCreatedAtDesc(Long userId, Long questionId);

    Optional<UserWrongAnswer> findByUserIdAndQuestionId(Long userId, Long questionId);

    Page<UserWrongAnswer> findByUserId(Long userId, Pageable pageable);
}
