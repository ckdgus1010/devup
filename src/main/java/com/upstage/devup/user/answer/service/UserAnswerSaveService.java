package com.upstage.devup.user.answer.service;

import com.upstage.devup.global.entity.*;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.question.dto.QuestionDetailDto;
import com.upstage.devup.question.service.QuestionService;
import com.upstage.devup.user.answer.dto.UserAnswerDetailDto;
import com.upstage.devup.user.answer.dto.UserAnswerSaveRequest;
import com.upstage.devup.user.answer.repository.AnswerUserRepository;
import com.upstage.devup.user.answer.repository.UserAnswerRepository;
import com.upstage.devup.user.answer.repository.UserAnswerStatRepository;
import com.upstage.devup.user.answer.repository.UserWrongAnswerRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAnswerSaveService {

    private final QuestionService questionService;

    private final AnswerUserRepository answerUserRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final UserAnswerStatRepository userAnswerStatRepository;
    private final UserWrongAnswerRepository userWrongAnswerRepository;

    /**
     * 사용자가 제출한 답안 저장
     *
     * @param userId  사용자 ID
     * @param request 사용자 답안 요청
     * @return 저장된 답안 정보
     * @throws EntityNotFoundException 사용자 또는 문제를 찾을 수 없을 때 발생
     */
    @Transactional
    public UserAnswerDetailDto saveUserAnswer(long userId, UserAnswerSaveRequest request) {
        if (request == null) {
            throw new EntityNotFoundException("면접 질문을 찾을 수 없습니다.");
        }

        if (!answerUserRepository.existsById(userId)) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        QuestionDetailDto questionDetailDto = questionService.getQuestion(request.getQuestionId());

        UserAnswerContext context = new UserAnswerContext(userId, request.getQuestionId());

        // 풀이 이력 조회 및 갱신
        updateUserAnswerStat(request, context);

        // 틀린 문제는 오답 노트에 저장
        updateWrongNote(request, context);

        // 사용자 답안 이력 저장
        UserAnswer userAnswer = updateUserAnswerHistory(request, context);

        return UserAnswerDetailDto.builder()
                .userId(userAnswer.getUser().getId())
                .questionId(request.getQuestionId())
                .title(questionDetailDto.getTitle())
                .questionText(questionDetailDto.getQuestionText())
                .category(questionDetailDto.getCategory())
                .level(questionDetailDto.getLevel())
                .userAnswerId(userAnswer.getId())
                .answerText(userAnswer.getAnswerText())
                .isCorrect(userAnswer.getIsCorrect())
                .createdAt(userAnswer.getCreatedAt())
                .build();
    }

    /**
     * 사용자 답안 이력 저장
     *
     * @param request 사용자 답안 요청
     * @param context 사용자, 문제, 기준  시간 정보
     * @return 저장된 사용자 답안
     */
    private UserAnswer updateUserAnswerHistory(UserAnswerSaveRequest request, UserAnswerContext context) {
        UserAnswer userAnswer = UserAnswer.builder()
                .user(context.getUser())
                .question(context.getQuestion())
                .answerText(request.getAnswerText())
                .isCorrect(request.getIsCorrect() ? 1 : 0)
                .createdAt(context.getNow())
                .build();

        // 저장
        return userAnswerRepository.save(userAnswer);
    }

    /**
     * 틀린 문제를 오답 노트에 저장
     *
     * @param request 사용자 답안 요청
     * @param context 사용자, 문제, 기준  시간 정보
     */
    private void updateWrongNote(UserAnswerSaveRequest request, UserAnswerContext context) {
        if (request.getIsCorrect()) {
            return;
        }

        UserWrongAnswer entity = userWrongAnswerRepository
                .findByUserIdAndQuestionId(context.getUser().getId(), request.getQuestionId())
                .orElseGet(
                        () -> UserWrongAnswer.builder()
                                .user(context.getUser())
                                .question(context.getQuestion())
                                .createdAt(context.getNow())
                                .build()
                );

        userWrongAnswerRepository.save(entity);
    }

    /**
     * 풀이 이력 조회 및 갱신
     *
     * @param request 사용자 답안 요청
     * @param context 사용자, 문제, 기준  시간 정보
     */
    private void updateUserAnswerStat(UserAnswerSaveRequest request, UserAnswerContext context) {
        // 엔티티 조회
        UserAnswerStat userAnswerStat = userAnswerStatRepository
                .findByUserIdAndQuestionId(context.getUser().getId(), request.getQuestionId())
                .orElseGet(
                        () -> UserAnswerStat.builder()
                                .user(context.getUser())
                                .question(context.getQuestion())
                                .correctCount(0)
                                .wrongCount(0)
                                .firstSolvedAt(context.getNow())
                                .lastSolvedAt(context.getNow())
                                .build()
                );

        // 이력 업데이트
        if (request.getIsCorrect()) {
            userAnswerStat.increaseCorrectCount();
        } else {
            userAnswerStat.increaseWrongCount();
        }

        userAnswerStat.setLastSolvedAt(context.getNow());
        userAnswerStatRepository.save(userAnswerStat);
    }

    @Getter
    private static class UserAnswerContext {

        private final User user;
        private final Question question;
        private final LocalDateTime now;

        public UserAnswerContext(Long userId, Long questionId) {
            this.user = User.builder().id(userId).build();
            this.question = Question.builder().id(questionId).build();
            this.now = LocalDateTime.now();
        }
    }

}
