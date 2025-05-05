package com.upstage.devup.answer.repository;

import com.upstage.devup.answer.domain.entity.UserWrongAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWrongAnswerRepository extends JpaRepository<UserWrongAnswer, Long> {
}
