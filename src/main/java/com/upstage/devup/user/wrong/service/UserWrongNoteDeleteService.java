package com.upstage.devup.user.wrong.service;

import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.user.wrong.repository.UserWrongNoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserWrongNoteDeleteService {

    private final UserWrongNoteRepository userWrongNoteRepository;

    @Transactional
    public void deleteWrongNote(long userId, long questionId) {
        if (!userWrongNoteRepository.existsByUserIdAndQuestionId(userId, questionId)) {
            throw new EntityNotFoundException("오답노트를 찾을 수 없습니다.");
        }

        userWrongNoteRepository.deleteByUserIdAndQuestionId(userId, questionId);
    }

}
