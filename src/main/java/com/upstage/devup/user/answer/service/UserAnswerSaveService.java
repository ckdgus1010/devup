package com.upstage.devup.user.answer.service;

import com.upstage.devup.global.entity.Question;
import com.upstage.devup.global.entity.User;
import com.upstage.devup.global.entity.UserAnswer;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.question.service.QuestionService;
import com.upstage.devup.user.account.service.UserAccountService;
import com.upstage.devup.user.answer.dto.UserAnswerDetailDto;
import com.upstage.devup.user.answer.dto.UserAnswerSaveRequest;
import com.upstage.devup.user.answer.repository.UserAnswerRepository;
import com.upstage.devup.user.statistics.service.UserAnswerStatService;
import com.upstage.devup.user.wrong.service.UserWrongAnswerSaveService;
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
    private final UserAccountService userAccountService;
    private final UserAnswerStatService userAnswerStatService;
    private final UserWrongAnswerSaveService userWrongAnswerSaveService;

    private final UserAnswerRepository userAnswerRepository;

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

        User user = userAccountService.getUser(userId)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        Question question = questionService.getQuestion(request.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("면접 질문을 찾을 수 없습니다."));

        LocalDateTime now = LocalDateTime.now();

        // 풀이 이력 조회 및 갱신
        userAnswerStatService.updateUserAnswerStat(request.getIsCorrect(), user, question, now);

        // 틀린 문제는 오답 노트에 저장
        userWrongAnswerSaveService.updateWrongNote(request.getIsCorrect(), user, question, now);

        // 사용자 답안 이력 저장
        UserAnswer userAnswer = updateUserAnswerHistory(request, user, question, now);

        return UserAnswerDetailDto.builder()
                .userId(userAnswer.getUser().getId())
                .questionId(request.getQuestionId())
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
    private UserAnswer updateUserAnswerHistory(UserAnswerSaveRequest request, User user, Question question, LocalDateTime now) {
        UserAnswer userAnswer = UserAnswer.builder()
                .user(user)
                .question(question)
                .answerText(request.getAnswerText())
                .feedback(request.getFeedback())
                .isCorrect(request.getIsCorrect() ? 1 : 0)
                .createdAt(now)
                .build();

        return userAnswerRepository.save(userAnswer);
    }

}
