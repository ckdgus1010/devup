package com.upstage.devup.user.answer.repository;

import com.upstage.devup.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerUserRepository extends JpaRepository<User, Long> {

}
