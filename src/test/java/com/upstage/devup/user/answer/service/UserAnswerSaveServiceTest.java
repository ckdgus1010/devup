package com.upstage.devup.user.answer.service;

import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.question.dto.QuestionDetailDto;
import com.upstage.devup.question.service.BoardService;
import com.upstage.devup.user.answer.domain.dto.UserAnswerDetailDto;
import com.upstage.devup.user.answer.domain.dto.UserAnswerSaveRequest;
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
    private BoardService boardService;

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
        QuestionDetailDto questionDetailDto = boardService.getQuestion(questionId);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getQuestionId()).isEqualTo(questionId);
        assertThat(result.getAnswerText()).isEqualTo(answerText);
        assertThat(result.getIsCorrect()).isEqualTo(isCorrect);

        assertThat(result.getTitle()).isEqualTo(questionDetailDto.getTitle());
        assertThat(result.getQuestionText()).isEqualTo(questionDetailDto.getQuestionText());
        assertThat(result.getCategory()).isEqualTo(questionDetailDto.getCategory());
        assertThat(result.getLevel()).isEqualTo(questionDetailDto.getLevel());
    }

    @Test
    @DisplayName("사용자 답안 저장 실패 - 사용자 ID가 null인 경우 EntityNotFoundException 발생")
    public void shouldThrowEntityException_whenUserIdIsNull() {
        // given
        Long userId = null;
        String errorMessage = "사용자 정보를 찾을 수 없습니다.";

        UserAnswerSaveRequest request = UserAnswerSaveRequest.builder().build();

        // when
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userAnswerSaveService.saveUserAnswer(userId, request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("사용자 답안 저장 실패 - 유효하지 않은 사용자 ID를 경우 EntityNotFoundException 발생")
    public void shouldThrowEntityException_whenUserIdIsUnavailable() {
        // given
        Long userId = 0L;
        String errorMessage = "사용자 정보를 찾을 수 없습니다.";

        UserAnswerSaveRequest request = UserAnswerSaveRequest.builder().build();

        // when
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userAnswerSaveService.saveUserAnswer(userId, request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("사용자 답안 저장 실패 - 요청 데이터가 null인 경우 EntityNotFoundException 발생")
    public void shouldThrowEntityException_whenRequestIsNull() {
        // given
        Long userId = 1L;
        String errorMessage = "면접 질문을 찾을 수 없습니다.";

        UserAnswerSaveRequest request = null;

        // when
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userAnswerSaveService.saveUserAnswer(userId, request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("사용자 답안 저장 실패 - 유효하지 않은 질문 ID를 경우 EntityNotFoundException 발생")
    public void shouldThrowEntityException_whenQuestionIdIsNull() {
        // given
        Long userId = 1L;
        Long questionId = 0L;
        String errorMessage = "면접 질문을 찾을 수 없습니다.";

        UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                .questionId(questionId)
                .build();

        // when
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userAnswerSaveService.saveUserAnswer(userId, request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }
}