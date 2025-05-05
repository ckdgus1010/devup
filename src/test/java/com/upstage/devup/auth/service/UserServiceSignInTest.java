package com.upstage.devup.auth.service;

import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.auth.domain.dto.SignInRequestDto;
import com.upstage.devup.auth.domain.dto.SignUpRequestDto;
import com.upstage.devup.auth.domain.dto.SignUpResponseDto;
import com.upstage.devup.auth.exception.InvalidLoginException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceSignInTest {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("로그인 성공")
    public void successToSignIn() {
        // given
        // 회원가입을 먼저 진행 -> 유효한 유저 생성
        String loginId = "user3";
        String password = "1111";
        String nickname = "newUser";
        String email = "newUser@devup.com";

        SignUpResponseDto signUpResult = userService.signUp(
                SignUpRequestDto.builder()
                .loginId(loginId)
                .password(password)
                .nickname(nickname)
                .email(email)
                .build()
        );

        // 로그인 요청 DTO
        SignInRequestDto request = SignInRequestDto.builder()
                .loginId(loginId)
                .password(password)
                .build();

        // when
        String token = userService.signIn(request);

        // then
        // 토큰이 유효하며 유저 ID를 가지고 잇어야 함
        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.validateJwtToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserIdFromJwtToken(token)).isEqualTo(signUpResult.getId());
    }

    @Test
    @DisplayName("로그인 실패 - 로그인 요청 데이터가 null인 경우")
    public void failToSignInUsingNullRequest() {
        // given
        SignInRequestDto request = null;
        String errorMessage = "유효하지 않는 요청입니다.";

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.signIn(request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("로그인 실패 - 로그인 ID가 null인 경우")
    public void failToSignInUsingNullLoginId() {
        // given
        SignInRequestDto request = SignInRequestDto.builder()
                .loginId(null)
                .password("1234")
                .build();
        String errorMessage = "아이디 또는 비밀번호를 확인해주세요.";

        // when & then
        InvalidLoginException exception = assertThrows(
                InvalidLoginException.class,
                () -> userService.signIn(request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("로그인 실패 - 로그인 ID가 빈 값인 경우")
    public void failToSignInUsingEmptyLoginId() {
        // given
        SignInRequestDto request = SignInRequestDto.builder()
                .loginId("")
                .password("1234")
                .build();
        String errorMessage = "아이디 또는 비밀번호를 확인해주세요.";

        // when & then
        InvalidLoginException exception = assertThrows(
                InvalidLoginException.class,
                () -> userService.signIn(request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("로그인 실패 - 등록되지 않은 로그인 ID를 사용한 경우")
    public void failToSignInUsingUnregisteredLoginId() {
        // given
        SignInRequestDto request = SignInRequestDto.builder()
                .loginId("algo")
                .password("1234")
                .build();
        String errorMessage = "아이디 또는 비밀번호를 확인해주세요.";

        // when & then
        InvalidLoginException exception = assertThrows(
                InvalidLoginException.class,
                () -> userService.signIn(request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 null인 경우")
    public void failToSignInUsingNullPassword() {
        // given
        SignInRequestDto request = SignInRequestDto.builder()
                .loginId("user1")
                .password(null)
                .build();
        String errorMessage = "아이디 또는 비밀번호를 확인해주세요.";

        // when & then
        InvalidLoginException exception = assertThrows(
                InvalidLoginException.class,
                () -> userService.signIn(request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 빈 값인 경우")
    public void failToSignInUsingEmptyPassword() {
        // given
        SignInRequestDto request = SignInRequestDto.builder()
                .loginId("user1")
                .password("")
                .build();
        String errorMessage = "아이디 또는 비밀번호를 확인해주세요.";

        // when & then
        InvalidLoginException exception = assertThrows(
                InvalidLoginException.class,
                () -> userService.signIn(request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 일치하지 않는 경우")
    public void failToSignInUsingUnmatchedPassword() {
        // given
        SignInRequestDto request = SignInRequestDto.builder()
                .loginId("user1")
                .password("scv")
                .build();
        String errorMessage = "아이디 또는 비밀번호를 확인해주세요.";

        // when & then
        InvalidLoginException exception = assertThrows(
                InvalidLoginException.class,
                () -> userService.signIn(request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }
}
