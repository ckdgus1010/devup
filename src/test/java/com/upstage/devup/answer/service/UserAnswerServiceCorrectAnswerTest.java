package com.upstage.devup.answer.service;

import com.upstage.devup.answer.domain.dto.UserAnswerDetailDto;
import com.upstage.devup.answer.domain.dto.UserAnswerSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: 마이페이지 통계 관련 기능 구현 후 테스트 코드 수정할 것
@SpringBootTest
public class UserAnswerServiceCorrectAnswerTest {

    @Autowired
    private UserAnswerService userAnswerService;

    @Test
    @DisplayName("사용자의 정답 답안 저장 성공 - 사용자가 제출한 답안이 정답인 경우 UserCorrectAnswer도 함께 저장됨")
    public void successToSaveUserCorrectAnswer() {
        // given
        Long userId = 1L;
        Long questionId = 2L;
        String answerText = "의존성 주입은 객체 간 의존 관계를 외부에서 주입하여 결합도를 낮추는 방법입니다.";
        boolean isCorrect = true;

        UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                .questionId(questionId)
                .answerText(answerText)
                .isCorrect(isCorrect)
                .build();

        // when
        Long result = userAnswerService.saveUserAnswer(userId, request);

        // then
        assertThat(result).isGreaterThan(0);
    }

    @Test
    @DisplayName("사용자의 정답 답안 저장 실패 - 사용자 ID가 유효하지 않는 경우")
    public void failToSaveUserCorrectAnswerUsingUnavailableUserId() {
        // given
        Long userId = 0L;
        Long questionId = 2L;
        UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                .questionId(questionId)
                .build();

        // when
        Long result = userAnswerService.saveUserAnswer(userId, request);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("사용자의 정답 답안 저장 실패 - 질문 ID가 유효하지 않는 경우")
    public void failToSaveUserCorrectAnswerUsingUnavailableQuestionId() {
        // given
        Long userId = 1L;
        Long questionId = 0L;
        UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                .questionId(questionId)
                .build();

        // when
        Long result = userAnswerService.saveUserAnswer(userId, request);

        // then
        assertThat(result).isNull();
    }
}
