package com.upstage.devup.admin.level.controller;

import com.upstage.devup.Util;
import com.upstage.devup.admin.level.dto.LevelDto;
import com.upstage.devup.admin.level.dto.LevelPageDto;
import com.upstage.devup.admin.level.service.LevelService;
import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.global.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LevelController.class)
@Import(SecurityConfig.class)
class LevelControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    LevelService levelService;

    private static final int LEVELS_PER_PAGE = 10;
    private static final long ADMIN_USER_ID = 1L;

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    private static final String URL_TEMPLATE = "/api/admin/levels";

    @Nested
    @DisplayName("단건 조회 API 테스트")
    public class SingleQueryApiTest {

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("유효한 levelId를 사용하는 경우 - 200 반환")
            public void shouldReturn200_whenLevelIdIsValid() throws Exception {
                // given
                long levelId = 1L;
                String levelName = "난이도 이름";
                LocalDateTime createdAt = LocalDateTime.now();
                LocalDateTime modifiedAt = null;

                when(levelService.getLevel(eq(levelId)))
                        .thenReturn(new LevelDto(levelId, levelName, createdAt, modifiedAt));

                // when & then
                mockMvc.perform(get(URL_TEMPLATE + "/" + levelId)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.levelId").value(levelId))
                        .andExpect(jsonPath("$.levelName").value(levelName))
                        .andExpect(jsonPath("$.createdAt").value(Util.formatToIsoLocalDateTime(createdAt)))
                        .andExpect(jsonPath("$.modifiedAt").value(modifiedAt));

                verify(levelService).getLevel(eq(levelId));
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @Test
            @DisplayName("로그인하지 않고 호출한 경우 - 401 반환")
            public void shouldReturn401_whenUserDoesNotSignIn() throws Exception {
                // given
                long levelId = 1L;

                // when & then
                mockMvc.perform(get(URL_TEMPLATE + "/" + levelId))
                        .andExpect(status().isUnauthorized());
            }

            @Test
            @DisplayName("관리자로 로그인하지 않고 호출한 경우 - 403 반환")
            public void shouldReturn403_whenUserDoesNotSignInToAdmin() throws Exception {
                // given
                long levelId = 1L;
                long userId = 2L;

                // when & then
                mockMvc.perform(get(URL_TEMPLATE + "/" + levelId)
                                .with(Util.getAuthentication(userId, ROLE_USER)))
                        .andExpect(status().isForbidden());
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "-1",
                    "0",
                    "9223372036854775807"
            })
            @DisplayName("존재하지 않는 leverId를 사용하는 경우 - 404 반환")
            public void shouldReturn404_whenLevelIdDoesNotExist(long levelId) throws Exception {
                // given
                final String CODE_NOT_FOUND = "NOT_FOUND";
                final String ERR_MSG = "존재하지 않는 난이도입니다.";

                when(levelService.getLevel(eq(levelId)))
                        .thenThrow(new EntityNotFoundException(ERR_MSG));

                // when & then
                mockMvc.perform(get(URL_TEMPLATE + "/" + levelId)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN)))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(CODE_NOT_FOUND))
                        .andExpect(jsonPath("$.message").value(ERR_MSG));

                verify(levelService).getLevel(eq(levelId));
            }
        }
    }

    @Nested
    @DisplayName("페이지 조회 API 테스트")
    public class PageQueryApiTest {

        private List<LevelDto> createDummyLevelDtoList(int size) {
            List<LevelDto> list = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                list.add(new LevelDto(i, "난이도 " + i, LocalDateTime.now(), null));
            }

            return list;
        }

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("유효한 요청이 온 경우 - 200 OK 반환")
            public void shouldReturn200_whenRequestIsValid() throws Exception {
                // given
                int pageNumber = 0;

                LevelPageDto mockResult = new LevelPageDto(
                        new PageImpl<>(createDummyLevelDtoList(LEVELS_PER_PAGE)));

                when(levelService.getLevels(eq(pageNumber)))
                        .thenReturn(mockResult);

                // when & then
                mockMvc.perform(get(URL_TEMPLATE)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN))
                                .queryParam("pageNumber", String.valueOf(pageNumber)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.content").isNotEmpty())
                        .andExpect(jsonPath("$.number").value(pageNumber))
                        .andExpect(jsonPath("$.size").value(mockResult.getSize()))
                        .andExpect(jsonPath("$.totalPages").value(mockResult.getTotalPages()))
                        .andExpect(jsonPath("$.totalElements").value(mockResult.getTotalElements()))
                        .andExpect(jsonPath("$.hasPrevious").value(mockResult.isHasPrevious()))
                        .andExpect(jsonPath("$.hasNext").value(mockResult.isHasNext()));

                verify(levelService).getLevels(eq(pageNumber));
            }

            @Test
            @DisplayName("pageNumber가 음수인 경우 - 200 OK, 첫번째 페이지 반환")
            public void shouldReturn200_whenPageNumberIsNegative() throws Exception {
                // given
                int pageNumber = -1;

                LevelPageDto mockResult = new LevelPageDto(
                        new PageImpl<>(createDummyLevelDtoList(LEVELS_PER_PAGE)));

                when(levelService.getLevels(eq(pageNumber)))
                        .thenReturn(mockResult);

                // when & then
                mockMvc.perform(get(URL_TEMPLATE)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN))
                                .queryParam("pageNumber", String.valueOf(pageNumber)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.content").isNotEmpty())
                        .andExpect(jsonPath("$.number").value(0))
                        .andExpect(jsonPath("$.size").value(mockResult.getSize()))
                        .andExpect(jsonPath("$.totalPages").value(mockResult.getTotalPages()))
                        .andExpect(jsonPath("$.totalElements").value(mockResult.getTotalElements()))
                        .andExpect(jsonPath("$.hasPrevious").value(mockResult.isHasPrevious()))
                        .andExpect(jsonPath("$.hasNext").value(mockResult.isHasNext()));

                verify(levelService).getLevels(eq(pageNumber));
            }

            @Test
            @DisplayName("pageNumber가 전체 페이지보다 큰 경우 - 200 OK, 빈 페이지 반환")
            public void shouldReturn200AndEmptyLevelPageDto_whenPageNumberIsGreaterThanTotalPages() throws Exception {
                // given
                int pageNumber = 123456789;

                LevelPageDto mockResult = new LevelPageDto(
                        new PageImpl<>(List.of(), PageRequest.of(pageNumber, LEVELS_PER_PAGE), 0)
                );

                when(levelService.getLevels(eq(pageNumber)))
                        .thenReturn(mockResult);

                // when & then
                mockMvc.perform(get(URL_TEMPLATE)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN))
                                .queryParam("pageNumber", String.valueOf(pageNumber)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.content").isEmpty())
                        .andExpect(jsonPath("$.number").value(pageNumber))
                        .andExpect(jsonPath("$.size").value(mockResult.getSize()))
                        .andExpect(jsonPath("$.totalPages").value(mockResult.getTotalPages()))
                        .andExpect(jsonPath("$.totalElements").value(mockResult.getTotalElements()))
                        .andExpect(jsonPath("$.hasPrevious").value(mockResult.isHasPrevious()))
                        .andExpect(jsonPath("$.hasNext").value(mockResult.isHasNext()));

                verify(levelService).getLevels(eq(pageNumber));
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @Test
            @DisplayName("로그인하지 않은 경우 - 401 Unauthorized 반환")
            public void shouldReturn401_whenUserDoesNotSignIn() throws Exception {
                // given
                int pageNumber = 0;

                // when & then
                mockMvc.perform(get(URL_TEMPLATE)
                                .queryParam("pageNumber", String.valueOf(pageNumber)))
                        .andExpect(status().isUnauthorized());
            }

            @Test
            @DisplayName("로그인하지 않은 경우 - 403 Forbidden 반환")
            public void shouldReturn403_whenUserIsNotAdmin() throws Exception {
                // given
                int pageNumber = 0;
                long userId = 1L;

                // when & then
                mockMvc.perform(get(URL_TEMPLATE)
                                .with(Util.getAuthentication(userId, ROLE_USER))
                                .queryParam("pageNumber", String.valueOf(pageNumber)))
                        .andExpect(status().isForbidden());
            }
        }
    }
}