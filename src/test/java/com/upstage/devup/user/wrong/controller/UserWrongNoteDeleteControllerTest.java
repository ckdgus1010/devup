package com.upstage.devup.user.wrong.controller;

import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.user.wrong.service.UserWrongNoteDeleteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.upstage.devup.Util.getAuthentication;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserWrongNoteDeleteController.class)
@Import(SecurityConfig.class)
class UserWrongNoteDeleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserWrongNoteDeleteService userWrongNoteDeleteService;

    private static final String ROLE_USER = "ROLE_USER";
    private static final String URI_TEMPLATE = "/api/wrong/";

    @Nested
    @DisplayName("성공 테스트")
    public class SuccessCases {

        @Test
        @DisplayName("DB에 오답노트가 있는 경우 200 OK 응답")
        public void shouldReturn200_whenUserWrongAnswerExistsInDB() throws Exception {
            // given
            long userId = 1L;
            long questionId = 2L;

            // when & then
            mockMvc.perform(delete(URI_TEMPLATE + questionId)
                            .with(getAuthentication(userId, ROLE_USER)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    public class FailureCases {

        @Test
        @DisplayName("로그인하지 않은 경우 401 Unauthorized 응답")
        public void shouldReturn401_whenUserDoesNotSignIn() throws Exception {
            // given
            long questionId = 99999L;

            // when & then
            mockMvc.perform(delete(URI_TEMPLATE + questionId))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DB에 오답노트가 없는 경우 404 Not Found 응답")
        public void shouldReturn404_whenEntityDoesNotExistInDB() throws Exception {
            // given
            long userId = 1L;
            long questionId = 99999L;

            doThrow(new EntityNotFoundException("오답노트를 찾을 수 없습니다."))
                    .when(userWrongNoteDeleteService)
                    .deleteWrongNote(userId, questionId);

            // when & then
            mockMvc.perform(delete(URI_TEMPLATE + questionId)
                            .with(getAuthentication(userId, ROLE_USER)))
                    .andExpect(status().isNotFound());
        }
    }
}