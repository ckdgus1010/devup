package com.upstage.devup.auth.service;

import com.upstage.devup.auth.domain.dto.SignUpRequestDto;
import com.upstage.devup.auth.domain.dto.SignUpResponseDto;
import com.upstage.devup.global.exception.ValueAlreadyInUseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    private final String loginId = "qwerty";
    private final String password = "asdf1234";
    private final String nickname = "apple";
    private final String email = "apple@gmail.com";

    @Test
    @DisplayName("회원가입 성공")
    public void successToSignUp() {
        // given
        SignUpRequestDto request = SignUpRequestDto.builder()
                .loginId(loginId)
                .password(password)
                .nickname(nickname)
                .email(email)
                .build();

        // when
        SignUpResponseDto result = userService.signUp(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isGreaterThan(0);
        assertThat(result.getLoginId()).isEqualTo(loginId);
        assertThat(result.getNickname()).isEqualTo(nickname);
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("회원가입 실패 - 요청 데이터가 null인 경우 IllegalArgumentException 발생")
    public void failToSignUpUsingNullRequest() {
        // given
        SignUpRequestDto request = null;
        String errorMessage = "유효하지 않는 요청입니다.";

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.signUp(request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 로그인 ID로 가입하려는 경우 ValueAlreadyInUseException 발생")
    public void failToSignUpUsingUsedLoginId() {
        // given
        String usedLoginId = "user1";

        SignUpRequestDto request = SignUpRequestDto.builder()
                .loginId(usedLoginId)
                .password(password)
                .nickname(nickname)
                .email(email)
                .build();
        String errorMessage = "이미 사용 중입니다.";

        // when & then
        ValueAlreadyInUseException exception = assertThrows(
                ValueAlreadyInUseException.class,
                () -> userService.signUp(request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 닉네임로 가입하려는 경우 ValueAlreadyInUseException 발생")
    public void failToSignUpUsingUsedNickname() {
        // given
        String usedNickname = "개발자1";

        SignUpRequestDto request = SignUpRequestDto.builder()
                .loginId(loginId)
                .password(password)
                .nickname(usedNickname)
                .email(email)
                .build();
        String errorMessage = "이미 사용 중입니다.";

        // when & then
        ValueAlreadyInUseException exception = assertThrows(
                ValueAlreadyInUseException.class,
                () -> userService.signUp(request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 이메일로 가입하려는 경우 ValueAlreadyInUseException 발생")
    public void failToSignUpUsingUsedEmail() {
        // given
        String usedEmail = "admin@devup.com";

        SignUpRequestDto request = SignUpRequestDto.builder()
                .loginId(loginId)
                .password(password)
                .nickname(nickname)
                .email(usedEmail)
                .build();
        String errorMessage = "이미 사용 중입니다.";

        // when & then
        ValueAlreadyInUseException exception = assertThrows(
                ValueAlreadyInUseException.class,
                () -> userService.signUp(request)
        );

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

}