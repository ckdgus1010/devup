package com.upstage.devup.question.service;

import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.question.dto.QuestionDetailDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Page<QuestionDetailDto> questions = boardService.getQuestions(page);

        // then
        assertThat(questions.getContent().size()).isEqualTo(10);
    }

    @Transactional
    @Test
    @DisplayName("제목 검색 - 성공")
    public void successToSearchQuestionByTitle() {
        // given
        String title = "란?";
        int page = 0;

        // when
        Page<QuestionDetailDto> results = boardService.searchQuestionsByTitle(title, page);

        // then
        assertThat(results.getTotalElements()).isGreaterThan(0);
    }

    @Test
    @DisplayName("제목 검색 - 공백으로 검색 시 실패")
    public void failToSearchQuestionByTitleUsingSpace() {
        // given
        String title = "     ";
        int page = 0;

        // when
        Page<QuestionDetailDto> results = boardService.searchQuestionsByTitle(title, page);

        // then
        assertThat(results.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("제목 검색 - 검색어 길이가 2보다 작을 시 실패")
    public void failToSearchQuestionByTitleUsingShortTitle() {
        // given
        String title = "1";
        int page = 0;

        // when
        Page<QuestionDetailDto> results = boardService.searchQuestionsByTitle(title, page);

        // then
        assertThat(results.getTotalElements()).isEqualTo(0);
    }

    @Transactional
    @Test
    @DisplayName("면접 질문 조회 - 성공")
    public void successFindQuestionById() {
        // given
        Long questionId = 1L;

        // when
        QuestionDetailDto result = boardService.getQuestion(questionId);

        // then
        assertThat(result.getId()).isEqualTo(questionId);
    }

    @Test
    @DisplayName("면접 질문 조회 실패 - 유효하지 않은 ID로 조회하는 경우(EntityNotFoundException 반환)")
    public void failToGetQuestion_whenUsingInvalidId() {
        // given
        Long questionId = 0L;
        String errorMessage = "면접 질문을 찾을 수 없습니다.";

        // when & then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> boardService.getQuestion(questionId)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }
}