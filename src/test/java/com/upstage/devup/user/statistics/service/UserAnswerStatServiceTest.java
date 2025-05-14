package com.upstage.devup.user.statistics.service;

import com.upstage.devup.auth.exception.UnAuthenticatedException;
import com.upstage.devup.user.statistics.domain.dto.UserAnswerStatDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserAnswerStatServiceTest {

    @Autowired
    private UserAnswerStatService userAnswerStatService;

    @Test
    @DisplayName("사용자 문제 풀이 통계 조회 성공")
    public void shouldGetUserAnswerStat() {
        // given
        Long userId = 1L;

        // when
        UserAnswerStatDto result = userAnswerStatService.getUserAnswerStat(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTotalCount()).isGreaterThan(0);
        assertThat(result.getCorrectCount()).isGreaterThan(0);
        assertThat(result.getCorrectCount()).isBetween(0L, result.getTotalCount());
    }

    @Test
    @DisplayName("사용자 문제 풀이 통계 조회 실패 - 사용자 ID가 null이면 UnAuthenticatedException 발생")
    public void shouldThrowUnAuthenticatedException_whenUserIdIsNull() {
        // given
        Long userId = null;
        String errorMessage = "로그인이 필요합니다.";

        // when
        UnAuthenticatedException exception = Assertions.assertThrows(
                UnAuthenticatedException.class,
                () -> userAnswerStatService.getUserAnswerStat(userId)
        );

        // then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("사용자 문제 풀이 통계 조회 실패 - 유효하지 않은 사용자 ID로 조회 시 빈 객체를 반환")
    public void whenUserIdDoesNotExist_thenReturnEmptyStats() {
        // given
        Long userId = 0L;

        // when
        UserAnswerStatDto result = userAnswerStatService.getUserAnswerStat(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTotalCount()).isEqualTo(0L);
        assertThat(result.getCorrectCount()).isEqualTo(0L);
    }

}