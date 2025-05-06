package com.upstage.devup.answer.service;

import com.upstage.devup.answer.domain.dto.AnswerDetailDto;
import com.upstage.devup.global.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class AnswerServiceTest {

    @Autowired
    private AnswerService answerService;

    @Test
    @DisplayName("면접 질문에 해당하는 정답 조회 성공")
    public void shouldReturnAnswer_whenQuestionIdIsValid() {
        // given
        Long questionId = 1L;

        // when
        AnswerDetailDto result = answerService.getAnswerByQuestionId(questionId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getQuestionId()).isEqualTo(questionId);
    }

    @Test
    @DisplayName("정답 조회 실패 - null로 조회하는 경우 IllegalArgumentException 발생")
    public void shouldThrowIllegalArgumentException_whenQuestionIdIsNull() {
        // given
        Long questionId = null;
        String errorMessage = "유효하지 않은 ID입니다.";

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> answerService.getAnswerByQuestionId(questionId)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("정답 조회 실패 - 저장된 정답이 없는 경우 EntityNotFoundException 발생")
    public void shouldThrowEntityNotFoundException_whenAnswerIsNotFound() {
        // given
        Long questionId = Long.MAX_VALUE;
        String errorMessage = "정답을 찾을 수 없습니다.";

        // when & then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> answerService.getAnswerByQuestionId(questionId)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }
}