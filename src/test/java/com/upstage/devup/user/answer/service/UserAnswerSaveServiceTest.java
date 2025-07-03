package com.upstage.devup.user.answer.service;

import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.question.dto.QuestionDetailDto;
import com.upstage.devup.question.service.QuestionService;
import com.upstage.devup.user.answer.dto.UserAnswerDetailDto;
import com.upstage.devup.user.answer.dto.UserAnswerSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserAnswerSaveServiceTest {

    @Autowired
    private UserAnswerSaveService userAnswerSaveService;

    @Autowired
    private QuestionService questionService;

    @Transactional
    @Test
    @DisplayName("사용자의 오답 답안 저장 성공")
    public void shouldSaveUserAnswer_whenValidRequest() {
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
        UserAnswerDetailDto result = userAnswerSaveService.saveUserAnswer(userId, request);

        // then
        QuestionDetailDto questionDetailDto = questionService.getQuestion(userId, questionId);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getQuestionId()).isEqualTo(questionId);
        assertThat(result.getAnswerText()).isEqualTo(answerText);
        assertThat(result.getIsCorrect()).isEqualTo(isCorrect);
    }

    @Test
    @DisplayName("사용자 답안 저장 실패 - 유효하지 않은 사용자 ID를 경우 EntityNotFoundException 발생")
    public void shouldThrowEntityException_whenUserIdIsUnavailable() {
        // given
        long userId = 0L;
        String errorMessage = "사용자 정보를 찾을 수 없습니다.";

        UserAnswerSaveRequest request = UserAnswerSaveRequest.builder().build();

        // when
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userAnswerSaveService.saveUserAnswer(userId, request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }
}