package com.upstage.devup.user.wrong.service;

import com.upstage.devup.global.entity.Question;
import com.upstage.devup.global.entity.User;
import com.upstage.devup.global.entity.UserWrongAnswer;
import com.upstage.devup.user.answer.repository.UserWrongAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserWrongAnswerSaveService {

    private final UserWrongAnswerRepository userWrongAnswerRepository;

    @Transactional
    public void updateWrongNote(boolean isCorrect, User user, Question question, LocalDateTime now) {
        if (isCorrect) {
            return;
        }

        UserWrongAnswer entity = userWrongAnswerRepository
                .findByUserIdAndQuestionId(user.getId(), question.getId())
                .orElseGet(() ->
                        UserWrongAnswer.builder()
                                .user(user)
                                .question(question)
                                .createdAt(now)
                                .build()
                );

        userWrongAnswerRepository.save(entity);
    }
}
