package com.upstage.devup.answer.repository;

import com.upstage.devup.answer.domain.entity.UserWrongAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserWrongAnswerRepository extends JpaRepository<UserWrongAnswer, Long> {
    Optional<UserWrongAnswer> findTopByUserIdAndQuestionIdOrderByCreatedAtDesc(Long userId, Long questionId);
}
