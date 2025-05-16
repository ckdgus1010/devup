package com.upstage.devup.answer.service;

import com.upstage.devup.user.answer.domain.dto.UserAnswerSaveRequest;
import com.upstage.devup.user.answer.service.UserAnswerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserAnswerServiceWrongAnswerTest {

    @Autowired
    private UserAnswerService userAnswerService;

    @Test
    @DisplayName("사용자의 오답 답안 저장 성공 - 사용자가 제출한 답안이 오답인 경우 UserWrongAnswer도 함께 저장됨")
    public void successToSaveUserWrongAnswer() {
        // given
        Long userId = 1L;
        Long questionId = 2L;
        String answerText = "";
        boolean isCorrect = false;

        UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                .questionId(questionId)
                .answerText(answerText)
                .isCorrect(isCorrect)
                .build();

        // when
//        userAnswerService.saveUserAnswer(userId, request);
//
//        // then
//        UserAnswerDetailDto result = userAnswerService.getUserWrongAnswer(userId, questionId);
//
//        assertThat(result.getId()).isGreaterThan(0);
//        assertThat(result.getUserId()).isEqualTo(userId);
//        assertThat(result.getQuestionId()).isEqualTo(questionId);
    }

    @Test
    @DisplayName("사용자의 오답 답안 저장 실패 - 사용자 ID가 유효하지 않는 경우")
    public void failToSaveUserWrongAnswerUsingUnavailableUserId() {
        // given
        Long userId = 0L;
        Long questionId = 2L;
        UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                .questionId(questionId)
                .build();

        // when
//        userAnswerService.saveUserAnswer(userId, request);
//
//        // then
//        UserAnswerDetailDto result = userAnswerService.getUserWrongAnswer(userId, questionId);
//        assertThat(result).isNull();
    }

    @Test
    @DisplayName("사용자의 오답 답안 저장 실패 - 질문 ID가 유효하지 않는 경우")
    public void failToSaveUserWrongAnswerUsingUnavailableQuestionId() {
        // given
        Long userId = 1L;
        Long questionId = 0L;
        UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                .questionId(questionId)
                .build();

        // when
//        userAnswerService.saveUserAnswer(userId, request);
//
//        // then
//        UserAnswerDetailDto result = userAnswerService.getUserWrongAnswer(userId, questionId);
//        assertThat(result).isNull();
    }
}
