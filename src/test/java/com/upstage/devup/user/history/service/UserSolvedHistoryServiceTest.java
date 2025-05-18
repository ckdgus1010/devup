package com.upstage.devup.user.history.service;

import com.upstage.devup.global.exception.UnauthenticatedException;
import com.upstage.devup.user.history.dto.UserSolvedQuestionDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserSolvedHistoryServiceTest {

    @Autowired
    private UserSolvedHistoryService userSolvedHistoryService;

    @Transactional
    @Test
    @DisplayName("문제 풀이 이력 조회 성공")
    public void shouldReturnPage_whenUsingValidRequest() {
        // given
        Long userId = 1L;
        Integer pageNumber = 0;

        // when
        Page<UserSolvedQuestionDto> results = userSolvedHistoryService.getUserSolvedQuestions(userId, pageNumber);

        // then
        assertThat(results).isNotNull();
        assertThat(results.getNumber()).isEqualTo(pageNumber);
        assertThat(results.isFirst()).isTrue();
    }

    @Transactional
    @Test
    @DisplayName("문제 풀이 이력 조회 성공 - 페이지 번호가 null 이면 가장 첫번째 페이지를 조회")
    public void shouldReturnFirstPage_whenPageNumberIsNull() {
        // given
        Long userId = 1L;
        Integer pageNumber = null;
        Integer targetPageNumber = 0;

        // when
        Page<UserSolvedQuestionDto> results = userSolvedHistoryService.getUserSolvedQuestions(userId, pageNumber);

        // then
        assertThat(results).isNotNull();
        assertThat(results.getNumber()).isEqualTo(targetPageNumber);
        assertThat(results.isFirst()).isTrue();
    }

    @Transactional
    @Test
    @DisplayName("문제 풀이 이력 조회 성공 - 페이지 번호가 음수이면 가장 첫번째 페이지를 조회")
    public void shouldReturnFirstPage_whenPageNumberIsNegative() {
        // given
        Long userId = 1L;
        Integer pageNumber = -1;
        Integer targetPageNumber = 0;

        // when
        Page<UserSolvedQuestionDto> results = userSolvedHistoryService.getUserSolvedQuestions(userId, pageNumber);

        // then
        assertThat(results).isNotNull();
        assertThat(results.getNumber()).isEqualTo(targetPageNumber);
        assertThat(results.isFirst()).isTrue();
    }

    @Transactional
    @Test
    @DisplayName("문제 풀이 이력 조회 성공 - 페이지 번호가 음수이면 가장 첫번째 페이지를 조회")
    public void shouldThrowUnAuthenticatedException_whenUserIdIsNull() {
        // given
        Long userId = null;
        Integer pageNumber = 0;
        String errorMessage = "로그인이 필요합니다.";

        // when & then
        UnauthenticatedException exception = Assertions.assertThrows(
                UnauthenticatedException.class,
                () -> userSolvedHistoryService.getUserSolvedQuestions(userId, pageNumber)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

}