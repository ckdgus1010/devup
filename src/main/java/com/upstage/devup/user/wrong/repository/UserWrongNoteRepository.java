package com.upstage.devup.user.wrong.repository;

import com.upstage.devup.global.entity.UserWrongAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWrongNoteRepository extends JpaRepository<UserWrongAnswer, Long> {

    void deleteByUserIdAndQuestionId(long userId, long questionId);

    boolean existsByUserIdAndQuestionId(long userId, long questionId);

}
