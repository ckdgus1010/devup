package com.upstage.devup.auth.service;

import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.user.account.dto.UserAccountDto;
import com.upstage.devup.user.account.service.UserAccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserAccountServiceTest {

    @Autowired
    private UserAccountService userAccountService;

    @Test
    @DisplayName("유저 정보 조회 성공")
    public void shouldReturnUserAccountDto_whenValidRequest() {
        // given
        Long userId = 1L;

        // when
        UserAccountDto result = userAccountService.getUserAccount(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("유저 정보 조회 실패 - 사용자 ID가 null인 경우 EntityNotFoundException 발생")
    public void shouldThrowEntityNotFoundException_whenUserIdIsNull() {
        // given
        Long userId = null;
        String errorMessage = "회원 정보를 찾을 수 없습니다.";

        // when & then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () ->userAccountService.getUserAccount(userId)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("유저 정보 조회 실패 - 유효하지 않은 사용자 ID를 사용한 경우 EntityNotFoundException 발생")
    public void shouldThrowEntityNotFoundException_whenUserIdIsUnavailable() {
        // given
        Long userId = 0L;
        String errorMessage = "회원 정보를 찾을 수 없습니다.";

        // when & then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () ->userAccountService.getUserAccount(userId)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }
}
