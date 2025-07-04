package com.upstage.devup.user.wrong.service;

import com.upstage.devup.user.statistics.dto.WrongNoteSummaryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserWrongAnswerQueryServiceTest {

    @Autowired
    private UserWrongAnswerQueryService userWrongAnswerQueryService;

    @Transactional
    @Test
    @DisplayName("사용자 오답 노트 목록 조회 성공")
    public void shouldReturnWrongNoteSummaries_whenValidRequest() {
        // given
        Long userId = 1L;
        Integer pageNumber = 0;

        // when
        Page<WrongNoteSummaryDto> results = userWrongAnswerQueryService.getWrongNoteSummaries(userId, pageNumber);

        // then
        assertThat(results).isNotEmpty();
        results.forEach(
                result -> assertThat(result.getUserId()).isEqualTo(userId)
        );
    }

    @Test
    @DisplayName("사용자 오답 노트 목록 조회 결과 없음 - 유효하지 않은 사용자 ID를 사용한 경우 빈 페이지 반환")
    public void shouldReturnEmptyPage_whenUserIdIsUnavailable() {
        // given
        Long userId = 0L;
        Integer pageNumber = 0;

        // when
        Page<WrongNoteSummaryDto> results = userWrongAnswerQueryService.getWrongNoteSummaries(userId, pageNumber);

        // then
        assertThat(results).isEmpty();
        assertThat(results.getTotalElements()).isEqualTo(0L);
    }

    @Test
    @DisplayName("사용자 오답 노트 목록 조회 결과 없음 - 유효하지 않은 pageNumber를 사용한 경우 빈 페이지 반환")
    public void shouldReturnEmptyPage_whenPageNumberIsTooLarge() {
        // given
        Long userId = 1L;
        Integer pageNumber = 100_000_000;

        // when
        Page<WrongNoteSummaryDto> results = userWrongAnswerQueryService.getWrongNoteSummaries(userId, pageNumber);

        // then
        assertThat(results).isEmpty();
        assertThat(results.getTotalPages()).isGreaterThan(0);
        assertThat(results.getContent().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자 오답 노트 목록 조회 결과 없음 - 음의 pageNumber를 사용한 경우 빈 페이지 반환")
    public void shouldReturnEmptyPage_whenPageNumberIsNegative() {
        // given
        Long userId = 1L;
        Integer pageNumber = -1;
        String errorMessage = "Page index must not be less than zero";

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userWrongAnswerQueryService.getWrongNoteSummaries(userId, pageNumber)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }
}