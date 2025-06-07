package com.upstage.devup.bookmark.controller;

import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.bookmark.dto.BookmarkDetails;
import com.upstage.devup.bookmark.dto.BookmarkResponseDto;
import com.upstage.devup.bookmark.dto.BookmarksQueryDto;
import com.upstage.devup.bookmark.service.BookmarkService;
import com.upstage.devup.global.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.upstage.devup.Util.getAuthentication;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private static final String ROLE_USER = "ROLE_USER";

    @Nested
    @DisplayName("북마크 조회")
    public class BookmarkQuery {

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            private static final String URL_TEMPLATE = "/api/bookmarks";
            BookmarksQueryDto mockResult = new BookmarksQueryDto(new PageImpl<>(List.of(
                    new BookmarkDetails(1L, "제목1", "카테고리1", "난이도1", LocalDateTime.now()),
                    new BookmarkDetails(2L, "제목2", "카테고리2", "난이도2", LocalDateTime.now()),
                    new BookmarkDetails(3L, "제목3", "카테고리3", "난이도3", LocalDateTime.now()),
                    new BookmarkDetails(4L, "제목4", "카테고리4", "난이도4", LocalDateTime.now()),
                    new BookmarkDetails(5L, "제목5", "카테고리5", "난이도5", LocalDateTime.now()),
                    new BookmarkDetails(6L, "제목6", "카테고리6", "난이도6", LocalDateTime.now()),
                    new BookmarkDetails(7L, "제목7", "카테고리7", "난이도7", LocalDateTime.now()),
                    new BookmarkDetails(8L, "제목8", "카테고리8", "난이도8", LocalDateTime.now()),
                    new BookmarkDetails(9L, "제목9", "카테고리9", "난이도9", LocalDateTime.now()),
                    new BookmarkDetails(10L, "제목10", "카테고리10", "난이도10", LocalDateTime.now())
            )));

            @Test
            @DisplayName("유효한 사용자 ID, pageNumber를 사용한 경우 - 북마크 목록을 반환")
            public void shouldReturn200_whenRequestIdValid() throws Exception {
                // given
                long userId = 1L;
                int pageNumber = 0;

                when(bookmarkService.getBookmarks(eq(userId), eq(pageNumber)))
                        .thenReturn(mockResult);

                // when & then
                mockMvc.perform(get(URL_TEMPLATE)
                                .with(getAuthentication(userId, ROLE_USER))
                                .param("pageNumber", String.valueOf(pageNumber)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.contents.size()").value(10))
                        .andExpect(jsonPath("$.contents[0].questionId").value(1L))
                        .andExpect(jsonPath("$.contents[0].title").value("제목1"))
                        .andExpect(jsonPath("$.contents[0].category").value("카테고리1"))
                        .andExpect(jsonPath("$.contents[0].level").value("난이도1"))
                        .andExpect(jsonPath("$.currentPageNumber").value(0))
                        .andExpect(jsonPath("$.size").value(10))
                        .andExpect(jsonPath("$.totalPages").value(1))
                        .andExpect(jsonPath("$.totalElements").value(10))
                        .andExpect(jsonPath("$.hasPrevious").value(false))
                        .andExpect(jsonPath("$.hasNext").value(false));
            }

            @Test
            @DisplayName("pageNumber가 음수인 경우 - pageNumber가 0으로 조회된 결과를 반환")
            public void shouldReturn200_whenPageNumberIsNegative() throws Exception {
                // given
                long userId = 1L;
                int pageNumber = -1;

                when(bookmarkService.getBookmarks(eq(userId), eq(pageNumber)))
                        .thenReturn(mockResult);

                // when & then
                mockMvc.perform(get(URL_TEMPLATE)
                                .with(getAuthentication(userId, ROLE_USER))
                                .param("pageNumber", String.valueOf(pageNumber)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.contents.size()").value(10))
                        .andExpect(jsonPath("$.contents[0].questionId").value(1L))
                        .andExpect(jsonPath("$.contents[0].title").value("제목1"))
                        .andExpect(jsonPath("$.contents[0].category").value("카테고리1"))
                        .andExpect(jsonPath("$.contents[0].level").value("난이도1"))
                        .andExpect(jsonPath("$.currentPageNumber").value(0))
                        .andExpect(jsonPath("$.size").value(10))
                        .andExpect(jsonPath("$.totalPages").value(1))
                        .andExpect(jsonPath("$.totalElements").value(10))
                        .andExpect(jsonPath("$.hasPrevious").value(false))
                        .andExpect(jsonPath("$.hasNext").value(false));
            }

            @Test
            @DisplayName("존재하지 않는 사용자 ID로 조회하는 경우 - 빈 값을 반환")
            public void shouldReturn200_whenUserIdIsUnavailable() throws Exception {
                // given
                long userId = -1L;
                int pageNumber = 0;

                when(bookmarkService.getBookmarks(eq(userId), eq(pageNumber)))
                        .thenReturn(new BookmarksQueryDto(
                                new PageImpl<>(List.of(), PageRequest.of(pageNumber, 10), 0)
                        ));

                // when & then
                mockMvc.perform(get(URL_TEMPLATE)
                                .with(getAuthentication(userId, ROLE_USER))
                                .param("pageNumber", String.valueOf(pageNumber)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.contents.size()").value(0))
                        .andExpect(jsonPath("$.currentPageNumber").value(0))
                        .andExpect(jsonPath("$.size").value(10))
                        .andExpect(jsonPath("$.totalPages").value(0))
                        .andExpect(jsonPath("$.totalElements").value(0))
                        .andExpect(jsonPath("$.hasPrevious").value(false))
                        .andExpect(jsonPath("$.hasNext").value(false));
            }
        }
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
                                        .with(getAuthentication(userId, ROLE_USER)))
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
                                .with(getAuthentication(userId, ROLE_USER)))
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
                                .with(getAuthentication(userId, ROLE_USER)))
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
                                .with(getAuthentication(userId, ROLE_USER)))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                        .andExpect(jsonPath("$.message").value("존재하지 않는 북마크입니다."));

            }
        }
    }
}