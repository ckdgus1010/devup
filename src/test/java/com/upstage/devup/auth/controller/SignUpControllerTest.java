package com.upstage.devup.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.auth.dto.SignUpRequestDto;
import com.upstage.devup.auth.dto.SignUpResponseDto;
import com.upstage.devup.auth.service.UserAuthService;
import com.upstage.devup.global.exception.ValueAlreadyInUseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SignUpController.class)
@Import(SecurityConfig.class)
class SignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserAuthService userAuthService;

    private static final String URI_TEMPLATE = "/api/auth/signup";
    private static final String VALIDATION_FAILED_CODE = "VALIDATION_FAILED";
    private static final String EMPTY_LOGIN_ID_ERR_MSG = "아이디는 필수 항목입니다.";
    private static final String EMPTY_PASSWORD_ERR_MSG = "비밀번호는 필수 항목입니다.";
    private static final String EMPTY_NICKNAME_ERR_MSG = "닉네임은 필수 항목입니다.";
    private static final String EMAIL_FORMAT_ERR_MSG = "이메일 형식이 아닙니다.";

    private static final String VALUE_ALREADY_IN_USE = "VALUE_ALREADY_IN_USE";
    private static final String USED_LOGIN_ID_ERR_MSG = "이미 사용 중인 아이디입니다.";
    private static final String USED_NICKNAME_ERR_MSG = "이미 사용 중인 닉네임입니다.";
    private static final String USED_EMAIL_ERR_MSG = "이미 사용 중인 이메일입니다.";

    private String loginId = "qwerty";
    private String password = "1111";
    private String nickname = "nick";
    private String email = "test@devup.com";


    @BeforeEach
    void setUp() {
        loginId = "qwerty";
        password = "1111";
        nickname = "nick";
        email = "test@devup.com";
    }

    private SignUpRequestDto createSignUpRequestDto(String loginId, String password, String nickname, String email) {
        return SignUpRequestDto.builder()
                .loginId(loginId)
                .password(password)
                .nickname(nickname)
                .email(email)
                .build();
    }

    @Nested
    @DisplayName("회원가입 성공 케이스")
    public class SuccessCases {

        @Test
        @DisplayName("유효한 회원가입 데이터를 사용한 경우")
        public void shouldReturn200_whenValidRequest() throws Exception {
            // given
            SignUpRequestDto request = createSignUpRequestDto(loginId, password, nickname, email);
            SignUpResponseDto mockResponse = SignUpResponseDto.builder()
                    .id(1L)
                    .loginId(loginId)
                    .nickname(nickname)
                    .email(email)
                    .build();

            when(userAuthService.signUp(eq(request)))
                    .thenReturn(mockResponse);

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.loginId").value(request.getLoginId()))
                    .andExpect(jsonPath("$.nickname").value(request.getNickname()))
                    .andExpect(jsonPath("$.email").value(request.getEmail()));
        }
    }

    @Nested
    @DisplayName("회원가입 실패 케이스 - 유효하지 않은 회원가입 데이터를 사용한 경우")
    public class InvalidInputCases {

        @DisplayName("아이디 누락 - 400 Bad Request 응답")
        @ParameterizedTest
        @NullAndEmptySource
        public void shouldReturn400_whenLoginIdIsBlank(String blank) throws Exception {
            // given
            SignUpRequestDto request = createSignUpRequestDto(blank, password, nickname, email);

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(VALIDATION_FAILED_CODE))
                    .andExpect(jsonPath("$.message").value(EMPTY_LOGIN_ID_ERR_MSG));
        }

        @DisplayName("비밀번호 누락 - 400 Bad Request 응답")
        @ParameterizedTest
        @NullAndEmptySource
        public void shouldReturn400_whenPasswordIsBlank(String blank) throws Exception {
            // given
            SignUpRequestDto request = createSignUpRequestDto(loginId, blank, nickname, email);

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(VALIDATION_FAILED_CODE))
                    .andExpect(jsonPath("$.message").value(EMPTY_PASSWORD_ERR_MSG));
        }

        @DisplayName("닉네임 누락 - 400 Bad Request 응답")
        @ParameterizedTest
        @NullAndEmptySource
        public void shouldReturn400_whenNicknameIsBlank(String blank) throws Exception {
            // given
            SignUpRequestDto request = createSignUpRequestDto(loginId, password, blank, email);

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(VALIDATION_FAILED_CODE))
                    .andExpect(jsonPath("$.message").value(EMPTY_NICKNAME_ERR_MSG));
        }

        @DisplayName("이메일 누락 - 400 Bad Request 응답")
        @ParameterizedTest
        @NullAndEmptySource
        public void shouldReturn400_whenEmailIsBlank(String blank) throws Exception {
            // given
            SignUpRequestDto request = createSignUpRequestDto(loginId, password, nickname, blank);

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(VALIDATION_FAILED_CODE))
                    .andExpect(jsonPath("$.message").value(EMAIL_FORMAT_ERR_MSG));
        }

        @DisplayName("이메일 형식이 아닌 경우 400 Bad Request 응답")
        @ParameterizedTest
        @CsvSource({
                "@",
                "test@",
                "test@dev."
        })
        public void shouldReturn400_whenEmailIsNotWellFormed(String localEmail) throws Exception {
            // given
            SignUpRequestDto request = createSignUpRequestDto(loginId, password, nickname, localEmail);

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(VALIDATION_FAILED_CODE))
                    .andExpect(jsonPath("$.message").value(EMAIL_FORMAT_ERR_MSG));
        }
    }

    @Nested
    @DisplayName("회원가입 실패 케이스 - 실제 사용중인 값을 사용한 경우")
    public class FailureCases_UsingDuplicatedValue {

        @Test
        @DisplayName("중복된 아이디 사용 - 409 Conflict 응답")
        public void shouldReturn409_whenLoginIdIsAlreadyUsed() throws Exception {
            // given
            SignUpRequestDto request = createSignUpRequestDto(loginId, password, nickname, email);

            when(userAuthService.signUp(eq(request)))
                    .thenThrow(new ValueAlreadyInUseException(USED_LOGIN_ID_ERR_MSG));

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(VALUE_ALREADY_IN_USE))
                    .andExpect(jsonPath("$.message").value(USED_LOGIN_ID_ERR_MSG));
        }

        @Test
        @DisplayName("중복된 닉네임 사용 - 409 Conflict 응답")
        public void shouldReturn409_whenNicknameIsAlreadyUsed() throws Exception {
            // given
            SignUpRequestDto request = createSignUpRequestDto(loginId, password, nickname, email);

            when(userAuthService.signUp(eq(request)))
                    .thenThrow(new ValueAlreadyInUseException(USED_NICKNAME_ERR_MSG));

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(VALUE_ALREADY_IN_USE))
                    .andExpect(jsonPath("$.message").value(USED_NICKNAME_ERR_MSG));
        }

        @Test
        @DisplayName("중복된 이메일 사용 - 409 Conflict 응답")
        public void shouldReturn409_whenEmailIsAlreadyUsed() throws Exception {
            // given
            SignUpRequestDto request = createSignUpRequestDto(loginId, password, nickname, email);

            when(userAuthService.signUp(eq(request)))
                    .thenThrow(new ValueAlreadyInUseException(USED_EMAIL_ERR_MSG));

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(VALUE_ALREADY_IN_USE))
                    .andExpect(jsonPath("$.message").value(USED_EMAIL_ERR_MSG));
        }
    }
}