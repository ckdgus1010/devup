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
        long userId = 1L;
        int pageNumber = 0;

        // when
        Page<UserSolvedQuestionDto> results = userSolvedHistoryService.getUserSolvedQuestions(userId, pageNumber);

        // then
        assertThat(results).isNotNull();
        assertThat(results.getNumber()).isEqualTo(pageNumber);
        assertThat(results.isFirst()).isTrue();
    }

    @Transactional
    @Test
    @DisplayName("문제 풀이 이력 조회 성공 - 페이지 번호가 음수이면 가장 첫번째 페이지를 조회")
    public void shouldReturnFirstPage_whenPageNumberIsNegative() {
        // given
        long userId = 1L;
        int pageNumber = -1;
        int targetPageNumber = 0;

        // when
        Page<UserSolvedQuestionDto> results = userSolvedHistoryService.getUserSolvedQuestions(userId, pageNumber);

        // then
        assertThat(results).isNotNull();
        assertThat(results.getNumber()).isEqualTo(targetPageNumber);
        assertThat(results.isFirst()).isTrue();
    }

}