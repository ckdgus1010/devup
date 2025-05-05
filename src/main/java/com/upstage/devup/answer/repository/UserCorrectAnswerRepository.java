package com.upstage.devup.answer.repository;

import com.upstage.devup.answer.domain.entity.UserCorrectAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCorrectAnswerRepository extends JpaRepository<UserCorrectAnswer, Long> {
    Optional<UserCorrectAnswer> findTopByUserIdAndQuestionIdOrderByCreatedAtDesc(Long userId, Long questionId);
}
