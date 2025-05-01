package com.upstage.devup.answer.repository;

import com.upstage.devup.answer.domain.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByQuestion_Id(Long questionId);
}
