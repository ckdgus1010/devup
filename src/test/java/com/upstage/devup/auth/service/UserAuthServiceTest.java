package com.upstage.devup.auth.service;

import com.upstage.devup.auth.dto.SignUpRequestDto;
import com.upstage.devup.auth.dto.SignUpResponseDto;
import com.upstage.devup.global.exception.ValueAlreadyInUseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserAuthServiceTest {

    @Autowired
    private UserAuthService userAuthService;

    private String loginId;
    private String password;
    private String nickname;
    private String email;

    @BeforeEach
    public void setUp() {
        SignUpRequestDto request = SignUpRequestDto.builder()
                .loginId("dummyId")
                .password("dummyPw")
                .nickname("dummyNickname")
                .email("test@devup.com")
                .build();

        userAuthService.signUp(request);

        loginId = "qwerty";
        password = "asdf1234";
        nickname = "apple";
        email = "apple@gmail.com";
    }

    @Nested
    @DisplayName("회원가입 성공 케이스")
    public class SuccessCases {

        @Test
        @DisplayName("유효한 회원가입 요청 데이터를 사용한 경우")
        public void shouldReturnSignUpResponseDto_whenValidRequest() {
            // given
            SignUpRequestDto request = SignUpRequestDto.builder()
                    .loginId(loginId)
                    .password(password)
                    .nickname(nickname)
                    .email(email)
                    .build();

            // when
            SignUpResponseDto result = userAuthService.signUp(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isGreaterThan(0);
            assertThat(result.getLoginId()).isEqualTo(loginId);
            assertThat(result.getNickname()).isEqualTo(nickname);
            assertThat(result.getEmail()).isEqualTo(email);
        }
    }

    @Nested
    @DisplayName("회원가입 실패 케이스")
    public class FailureCases {

        @Test
        @DisplayName("중복된 로그인 ID로 가입하려는 경우 ValueAlreadyInUseException 발생")
        public void shouldThrowValueAlreadyInUseException_whenLoginIdIsAlreadyUsed() {
            // given
            String usedLoginId = "dummyId";

            SignUpRequestDto request = SignUpRequestDto.builder()
                    .loginId(usedLoginId)
                    .password(password)
                    .nickname(nickname)
                    .email(email)
                    .build();

            // when & then
            ValueAlreadyInUseException exception = assertThrows(
                    ValueAlreadyInUseException.class,
                    () -> userAuthService.signUp(request)
            );

            assertThat(exception.getMessage()).isEqualTo("이미 사용 중인 아이디입니다.");
        }

        @Test
        @DisplayName("중복된 닉네임으로 가입하려는 경우 ValueAlreadyInUseException 발생")
        public void shouldThrowValueAlreadyInUseException_whenNicknameIsAlreadyUsed() {
            // given
            String usedNickname = "dummyNickname";

            SignUpRequestDto request = SignUpRequestDto.builder()
                    .loginId(loginId)
                    .password(password)
                    .nickname(usedNickname)
                    .email(email)
                    .build();

            // when & then
            ValueAlreadyInUseException exception = assertThrows(
                    ValueAlreadyInUseException.class,
                    () -> userAuthService.signUp(request)
            );

            assertThat(exception.getMessage()).isEqualTo("이미 사용 중인 닉네임입니다.");
        }

        @Test
        @DisplayName("중복된 이메일로 가입하려는 경우 ValueAlreadyInUseException 발생")
        public void shouldThrowValueAlreadyInUseException_whenEmailIsAlreadyUsed() {
            // given
            String usedEmail = "test@devup.com";

            SignUpRequestDto request = SignUpRequestDto.builder()
                    .loginId(loginId)
                    .password(password)
                    .nickname(nickname)
                    .email(usedEmail)
                    .build();

            // when & then
            ValueAlreadyInUseException exception = assertThrows(
                    ValueAlreadyInUseException.class,
                    () -> userAuthService.signUp(request)
            );

            assertThat(exception.getMessage()).isEqualTo("이미 사용 중인 이메일입니다.");
        }
    }

}