package com.upstage.devup.answer.repository;

import com.upstage.devup.answer.domain.entity.UserCorrectAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCorrectAnswerRepository extends JpaRepository<UserCorrectAnswer, Long> {
}
