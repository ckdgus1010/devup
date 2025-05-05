package com.upstage.devup.answer.service;

import com.upstage.devup.answer.domain.dto.UserAnswerSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserAnswerServiceTest {

    @Autowired
    private UserAnswerService userAnswerService;

    @Test
    @DisplayName("사용자 작성 답안 저장 성공")
    public void successToSaveUserAnswer() {
        // given
        Long userId = 1L;
        Long questionId = 2L;
        String answerText = "의존성 주입은 Dependency Injection입니다.";
        boolean isCorrect = false;

        UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                .questionId(questionId)
                .answerText(answerText)
                .isCorrect(isCorrect)
                .build();

        // when
        Long result = userAnswerService.saveUserAnswer(userId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isGreaterThan(0);
    }
}