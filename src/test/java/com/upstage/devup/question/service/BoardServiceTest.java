package com.upstage.devup.question.service;

import com.upstage.devup.question.domain.dto.QuestionDetailDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Transactional
    @Test
    @DisplayName("면접 질문 페이지 조회 - 성공")
    public void successGetQuestions() {
        // given
        int page = 0;

        // when
        List<QuestionDetailDto> questions = boardService.getQuestions(page);

        // then
        assertThat(questions.size()).isEqualTo(10);
    }

    @Transactional
    @Test
    @DisplayName("면접 질문 조회 - 성공")
    public void successFindQuestionById() {
        // given
        Long questionId = 1L;

        // when
        QuestionDetailDto result = boardService.findById(questionId);

        // then
        assertThat(result.getId()).isEqualTo(questionId);
    }

    @Test
    @DisplayName("면접 질문 조회 - 유효하지 않은 ID로 조회 시 실패")
    public void failToFindQuestionById() {
        // given
        Long questionId = -1L;

        // when
        QuestionDetailDto dto = boardService.findById(questionId);

        // then
        assertThat(dto).isNull();
    }
}