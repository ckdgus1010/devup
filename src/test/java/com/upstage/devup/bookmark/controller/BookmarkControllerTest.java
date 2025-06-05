package com.upstage.devup.bookmark.controller;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.bookmark.dto.BookmarkResponseDto;
import com.upstage.devup.bookmark.service.BookmarkService;
import com.upstage.devup.global.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookmarkController.class)
@Import(SecurityConfig.class)
class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookmarkService bookmarkService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private RequestPostProcessor getAuthentication(Long userId) {
        AuthenticatedUser user = new AuthenticatedUser(userId);
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, null);
        return authentication(auth);
    }

    @Nested
    @DisplayName("신규 북마크 등록")
    public class BookmarkRegistration {

        private static final String URL_TEMPLATE = "/api/bookmarks/";

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("유효한 userId와 questionId를 사용한 경우")
            public void shouldReturn200_whenUserIdAndQuestionIdAreValid() throws Exception {
                // given
                Long userId = 1L;
                Long questionId = 2L;
                LocalDateTime now = LocalDateTime.now();

                BookmarkResponseDto mockResult = BookmarkResponseDto.builder()
                        .userId(userId)
                        .questionId(questionId)
                        .createdAt(now)
                        .build();

                when(bookmarkService.registerBookmark(eq(userId), eq(questionId)))
                        .thenReturn(mockResult);

                // when & then
                String expectedCreatedAt = now
                        .truncatedTo(ChronoUnit.SECONDS)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                mockMvc.perform(
                                post(URL_TEMPLATE + questionId)
                                        .with(getAuthentication(userId)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/json"))
                        .andExpect(jsonPath("$.userId").value(userId))
                        .andExpect(jsonPath("$.questionId").value(questionId))
                        .andExpect(jsonPath("$.createdAt").value(expectedCreatedAt));
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @Test
            @DisplayName("로그인을 하지 않은 경우 401 Unauthorized 응답")
            public void shouldReturn401_whenUserDoesNotSignIn() throws Exception {
                // given
                long questionId = 1L;

                // when & then
                mockMvc.perform(post(URL_TEMPLATE + questionId))
                        .andExpect(status().isUnauthorized());
            }

            @Test
            @DisplayName("유효한 질문 ID가 아닌 경우 404 Not Found 응답")
            public void shouldReturn404_whenQuestionDoesNotExistInDB() throws Exception {
                // given
                long userId = 1L;
                long questionId = -1L;

                doThrow(new EntityNotFoundException("존재하지 않는 질문입니다."))
                        .when(bookmarkService)
                        .registerBookmark(userId, questionId);

                // when & then
                mockMvc.perform(post(URL_TEMPLATE + questionId)
                                .with(getAuthentication(userId)))
                        .andExpect(status().isNotFound());
            }
        }

    }

    @Nested
    @DisplayName("북마크 삭제")
    public class BookmarkDeletion {

        private static final String URL_TEMPLATE = "/api/bookmarks/";

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("유효한 userId, questionId를 사용한 경우 - 저장된 북마크 삭제")
            public void shouldReturn200_whenUserIdAndQuestionIdAreValid() throws Exception {
                // given
                long userId = 1L;
                long questionId = 2L;
                LocalDateTime now = LocalDateTime.now();

                BookmarkResponseDto mockResult = BookmarkResponseDto.builder()
                        .userId(userId)
                        .questionId(questionId)
                        .createdAt(now)
                        .build();

                when(bookmarkService.deleteBookmark(eq(userId), eq(questionId)))
                        .thenReturn(mockResult);

                // when & then
                String expectedCreatedAt = now
                        .truncatedTo(ChronoUnit.SECONDS)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                mockMvc.perform(delete(URL_TEMPLATE + questionId)
                                .with(getAuthentication(userId)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.userId").value(userId))
                        .andExpect(jsonPath("$.questionId").value(questionId))
                        .andExpect(jsonPath("$.createdAt").value(expectedCreatedAt));
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @Test
            @DisplayName("로그인을 하지 않은 경우 401 Unauthorized 응답")
            public void shouldReturn401_whenUserDoesNotSignIn() throws Exception {
                // given
                long questionId = 1L;

                // when & then
                mockMvc.perform(post(URL_TEMPLATE + questionId))
                        .andExpect(status().isUnauthorized());
            }

            @Test
            @DisplayName("DB에 저장된 북마크가 없는 경우 - EntityNotFoundException 예외 발생")
            public void shouldReturn404_whenBookmarkDoesNotExistsInDB() throws Exception {
                // given
                long userId = 1L;
                long questionId = Long.MAX_VALUE;

                doThrow(new EntityNotFoundException("존재하지 않는 북마크입니다."))
                        .when(bookmarkService)
                        .deleteBookmark(userId, questionId);

                // when & then
                mockMvc.perform(delete(URL_TEMPLATE + questionId)
                                .with(getAuthentication(userId)))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                        .andExpect(jsonPath("$.message").value("존재하지 않는 북마크입니다."));

            }
        }
    }
}