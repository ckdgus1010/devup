package com.upstage.devup.user.answer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.user.answer.dto.UserAnswerDetailDto;
import com.upstage.devup.user.answer.dto.UserAnswerSaveRequest;
import com.upstage.devup.user.answer.service.UserAnswerSaveService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.upstage.devup.Util.getAuthentication;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Nested
@WebMvcTest(UserAnswerSaveController.class)
@Import(SecurityConfig.class)
class UserAnswerSaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserAnswerSaveService userAnswerSaveService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private static final String ROLE_USER = "ROLE_USER";
    private static final String URI_TEMPLATE = "/api/user/answer";

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @Test
        @DisplayName("사용자 답안 저장 성공")
        public void shouldReturnUserAnswerDetailDto_whenValidRequest() throws Exception {
            // given
            Long userId = 1L;
            Long questionId = 2L;
            String answerText = "유저 답안1";
            Boolean isCorrect = true;

            UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                    .questionId(questionId)
                    .answerText(answerText)
                    .isCorrect(isCorrect)
                    .build();

            UserAnswerDetailDto mockResult = UserAnswerDetailDto.builder()
                    .userId(userId)
                    .questionId(questionId)
                    .title("제목1")
                    .questionText("본문1")
                    .category("JAVA")
                    .level("중")
                    .userAnswerId(1L)
                    .answerText(answerText)
                    .isCorrect(isCorrect)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(userAnswerSaveService.saveUserAnswer(eq(userId), any(UserAnswerSaveRequest.class)))
                    .thenReturn(mockResult);

            // when & then
            String expectedCreatedAt = mockResult.getCreatedAt()
                    .truncatedTo(ChronoUnit.SECONDS)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            mockMvc.perform(post(URI_TEMPLATE)
                            .with(getAuthentication(userId, ROLE_USER))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.questionId").value(questionId))
                    .andExpect(jsonPath("$.title").value(mockResult.getTitle()))
                    .andExpect(jsonPath("$.questionText").value(mockResult.getQuestionText()))
                    .andExpect(jsonPath("$.category").value(mockResult.getCategory()))
                    .andExpect(jsonPath("$.level").value(mockResult.getLevel()))
                    .andExpect(jsonPath("$.userAnswerId").value(mockResult.getUserAnswerId()))
                    .andExpect(jsonPath("$.answerText").value(answerText))
                    .andExpect(jsonPath("$.isCorrect").value(isCorrect))
                    .andExpect(jsonPath("$.createdAt").value(expectedCreatedAt));
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCases {

        @Test
        @DisplayName("로그인하지 않은 경우")
        public void shouldThrowUnauthenticatedException_whenUserIsNull() throws Exception {
            // given
            Long questionId = 2L;
            String answerText = "유저 답안1";
            Boolean isCorrect = true;

            UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                    .questionId(questionId)
                    .answerText(answerText)
                    .isCorrect(isCorrect)
                    .build();

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("유효하지 않은 사용자 ID를 사용한 경우")
        public void shouldThrowEntityNotFoundException_whenUserIsUnavailable() throws Exception {
            // given
            Long userId = -1L;
            UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                    .questionId(2L)
                    .answerText("유저 답안1")
                    .isCorrect(true)
                    .build();

            when(userAnswerSaveService.saveUserAnswer(eq(userId), any(UserAnswerSaveRequest.class)))
                    .thenThrow(new EntityNotFoundException("사용자 정보를 찾을 수 없습니다."));

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .with(getAuthentication(userId, ROLE_USER))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("사용자 정보를 찾을 수 없습니다."));
        }

        @Test
        @DisplayName("요청 데이터가 null 인 경우")
        public void shouldThrowEntityNotFoundException_whenRequestIsNull() throws Exception {
            // given
            Long userId = 1L;

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .with(getAuthentication(userId, ROLE_USER))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("질문 ID가 null인 경우")
        public void shouldThrowBadRequestException_whenQuestionIdIsNull() throws Exception {
            // given
            Long userId = 1L;
            UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                    .answerText("유저 답안1")
                    .isCorrect(true)
                    .build();

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .with(getAuthentication(userId, ROLE_USER))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("유효하지 않은 질문 ID를 사용한 경우")
        public void shouldThrowEntityNotFoundException_whenQuestionIdIsUnavailable() throws Exception {
            // given
            Long userId = 1L;
            UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                    .questionId(-1L)
                    .answerText("유저 답안1")
                    .isCorrect(true)
                    .build();

            when(userAnswerSaveService.saveUserAnswer(eq(userId), any(UserAnswerSaveRequest.class)))
                    .thenThrow(new EntityNotFoundException("면접 질문을 찾을 수 없습니다."));

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .with(getAuthentication(userId, ROLE_USER))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("면접 질문을 찾을 수 없습니다."));
        }

        @Test
        @DisplayName("사용자 정답이 null인 경우")
        public void shouldThrowBadRequestException_whenAnswerTextIsNull() throws Exception {
            // given
            Long userId = 1L;
            UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                    .questionId(1L)
                    .isCorrect(true)
                    .build();

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .with(getAuthentication(userId, ROLE_USER))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("사용자 정답이 빈 값인 경우")
        public void shouldThrowBadRequestException_whenAnswerTextIsEmpty() throws Exception {
            // given
            Long userId = 1L;
            UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                    .questionId(1L)
                    .answerText("")
                    .isCorrect(true)
                    .build();

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .with(getAuthentication(userId, ROLE_USER))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("사용자 정답이 공백으로만 이루어진 경우")
        public void shouldThrowBadRequestException_whenAnswerTextIsBlank() throws Exception {
            // given
            Long userId = 1L;
            UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                    .questionId(1L)
                    .answerText("   ")
                    .isCorrect(true)
                    .build();

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .with(getAuthentication(userId, ROLE_USER))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("정답 유무가 null인 경우")
        public void shouldThrowBadRequestException_whenIsCorrectIsNull() throws Exception {
            // given
            Long userId = 1L;
            UserAnswerSaveRequest request = UserAnswerSaveRequest.builder()
                    .questionId(1L)
                    .answerText("유저 답안1")
                    .build();

            // when & then
            mockMvc.perform(post(URI_TEMPLATE)
                            .with(getAuthentication(userId, ROLE_USER))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}