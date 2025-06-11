package com.upstage.devup.admin.level.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upstage.devup.Util;
import com.upstage.devup.admin.level.dto.LevelAddRequest;
import com.upstage.devup.admin.level.dto.LevelDto;
import com.upstage.devup.admin.level.dto.LevelPageDto;
import com.upstage.devup.admin.level.dto.LevelUpdateRequest;
import com.upstage.devup.admin.level.service.LevelService;
import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.global.exception.DuplicatedResourceException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LevelController.class)
@Import(SecurityConfig.class)
class LevelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private LevelService levelService;

    private static final int LEVELS_PER_PAGE = 10;
    private static final long ADMIN_USER_ID = 1L;

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    private static final String URL_TEMPLATE = "/api/admin/levels";
    private static final String ERR_MSG_DUPLICATED_RESOURCE = "이미 사용중인 난이도입니다.";
    private static final String ERR_MSG_ENTITY_NOT_FOUND = "존재하지 않는 난이도입니다.";


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
            @DisplayName("관리자로 로그인하지 않은 경우 - 403 Forbidden 반환")
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

    @Nested
    @DisplayName("등록 API 테스트")
    public class RegistrationApiTest {

        @Nested
        @DisplayName("성공 테스트")
        public class SuccessCases {

            @Test
            @DisplayName("유효한 요청인 경우 - 200 OK 반환")
            public void shouldReturn200_whenRequestIsValid() throws Exception {
                // given
                long levelId = 1L;
                String levelName = "등록할 난이도";
                LocalDateTime createdAt = LocalDateTime.now();
                LocalDateTime modifiedAt = null;

                LevelAddRequest request = new LevelAddRequest(levelName);
                LevelDto mockResult = new LevelDto(levelId, levelName, createdAt, modifiedAt);

                when(levelService.addLevel(eq(request)))
                        .thenReturn(mockResult);

                // when & then
                mockMvc.perform(post(URL_TEMPLATE)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.levelId").value(levelId))
                        .andExpect(jsonPath("$.levelName").value(levelName))
                        .andExpect(jsonPath("$.createdAt").value(Util.formatToIsoLocalDateTime(createdAt)))
                        .andExpect(jsonPath("$.modifiedAt").value(modifiedAt));
            }
        }

        @Nested
        @DisplayName("실패 테스트")
        public class FailureCases {

            @Test
            @DisplayName("난이도 이름이 빈 값인 경우 - 400 BAD_REQUEST 반환")
            public void shouldReturn400_whenLevelNameIsBlank() throws Exception {
                // given
                String levelName = "";
                LevelAddRequest request = new LevelAddRequest(levelName);

                // when & then
                mockMvc.perform(post(URL_TEMPLATE)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                        .andExpect(jsonPath("$.message").value("난이도를 입력해 주세요."));
            }

            @Test
            @DisplayName("중복된 난이도 이름을 등록하려는 경우 - 400 BAD_REQUEST 반환")
            public void shouldReturn400_whenLevelNameIsDuplicated() throws Exception {
                // given
                String levelName = "중복된 난이도";
                LevelAddRequest request = new LevelAddRequest(levelName);

                when(levelService.addLevel(request))
                        .thenThrow(new DuplicatedResourceException(ERR_MSG_DUPLICATED_RESOURCE));

                // when & then
                mockMvc.perform(post(URL_TEMPLATE)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andDo(print())
                        .andExpect(status().isConflict())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(HttpStatus.CONFLICT.name()))
                        .andExpect(jsonPath("$.message").value(ERR_MSG_DUPLICATED_RESOURCE));
            }

            @Test
            @DisplayName("로그인하지 않은 경우 - 401 Unauthorized 반환")
            public void shouldReturn401_whenUserDoesNotSignIn() throws Exception {
                // given
                String levelName = "등록할 난이도";
                LevelAddRequest request = new LevelAddRequest(levelName);

                // when & then
                mockMvc.perform(post(URL_TEMPLATE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isUnauthorized());
            }

            @Test
            @DisplayName("관리자로 로그인하지 않은 경우 - 403 Forbidden 반환")
            public void shouldReturn403_whenUserIsNotAdmin() throws Exception {
                // given
                String levelName = "등록할 난이도";
                LevelAddRequest request = new LevelAddRequest(levelName);

                // when & then
                mockMvc.perform(post(URL_TEMPLATE)
                                .with(Util.getAuthentication(2L, ROLE_USER))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isForbidden());
            }
        }

    }

    @Nested
    @DisplayName("수정 API 테스트")
    public class UpdateApiTest {

        @Nested
        @DisplayName("성공 테스트")
        public class SuccessCases {

            @Test
            @DisplayName("유효한 요청인 경우 - 200 OK 반환")
            public void shouldReturn200_whenRequestIsValid() throws Exception {
                // given
                long levelId = 1L;
                String levelName = "수정할 난이도";
                LocalDateTime createdAt = LocalDateTime.now();
                LocalDateTime modifiedAt = LocalDateTime.now().plusSeconds(1L);

                LevelUpdateRequest request = new LevelUpdateRequest(levelId, levelName);

                when(levelService.updateLevel(eq(request)))
                        .thenReturn(new LevelDto(levelId, levelName, createdAt, modifiedAt));

                // when & then
                mockMvc.perform(patch(URL_TEMPLATE)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.levelId").value(levelId))
                        .andExpect(jsonPath("$.levelName").value(levelName))
                        .andExpect(jsonPath("$.createdAt").value(Util.formatToIsoLocalDateTime(createdAt)))
                        .andExpect(jsonPath("$.modifiedAt").value(Util.formatToIsoLocalDateTime(modifiedAt)));

                verify(levelService).updateLevel(eq(request));
            }
        }

        @Nested
        @DisplayName("실패 테스트")
        public class FailureCases {

            @Test
            @DisplayName("난이도 ID가 null인 경우 - 400 BAD_REQUEST 반환")
            public void shouldReturn400_whenLevelIdIsNull() throws Exception {
                // given
                Long levelId = null;
                String levelName = "수정할 난이도";

                LevelUpdateRequest request = new LevelUpdateRequest(levelId, levelName);

                // when & then
                mockMvc.perform(patch(URL_TEMPLATE)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                        .andExpect(jsonPath("$.message").value("난이도를 선택해 주세요."));
            }

            @Test
            @DisplayName("난이도 이름이 빈 값인 경우 - 400 BAD_REQUEST 반환")
            public void shouldReturn400_whenLevelNameIsBlank() throws Exception {
                // given
                long levelId = 1L;
                String levelName = "";

                LevelUpdateRequest request = new LevelUpdateRequest(levelId, levelName);

                // when & then
                mockMvc.perform(patch(URL_TEMPLATE)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                        .andExpect(jsonPath("$.message").value("난이도를 입력해 주세요."));
            }

            @Test
            @DisplayName("중복된 난이도 이름을 등록하려는 경우 - 400 BAD_REQUEST 반환")
            public void shouldReturn400_whenLevelNameIsDuplicated() throws Exception {
                // given
                long levelId = 1L;
                String levelName = "중복된 난이도";

                LevelUpdateRequest request = new LevelUpdateRequest(levelId, levelName);

                when(levelService.updateLevel(eq(request)))
                        .thenThrow(new DuplicatedResourceException(ERR_MSG_DUPLICATED_RESOURCE));

                // when & then
                mockMvc.perform(patch(URL_TEMPLATE)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andDo(print())
                        .andExpect(status().isConflict())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(HttpStatus.CONFLICT.name()))
                        .andExpect(jsonPath("$.message").value(ERR_MSG_DUPLICATED_RESOURCE));

                verify(levelService).updateLevel(eq(request));
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "-1",
                    "0",
                    "9223372036854775807"
            })
            @DisplayName("존재하지 않는 난이도 ID를 사용하려는 경우 - 404 NOT_FOUND 반환")
            public void shouldReturn404_whenLevelIdDoesNotExist(long levelId) throws Exception {
                // given
                String levelName = "중복된 난이도";
                LevelUpdateRequest request = new LevelUpdateRequest(levelId, levelName);

                when(levelService.updateLevel(eq(request)))
                        .thenThrow(new EntityNotFoundException(ERR_MSG_ENTITY_NOT_FOUND));

                // when & then
                mockMvc.perform(patch(URL_TEMPLATE)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andDo(print())
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.name()))
                        .andExpect(jsonPath("$.message").value(ERR_MSG_ENTITY_NOT_FOUND));

                verify(levelService).updateLevel(eq(request));
            }

            @Test
            @DisplayName("로그인하지 않은 경우 - 401 Unauthorized 반환")
            public void shouldReturn401_whenUserDoesNotSignIn() throws Exception {
                // given
                long levelId = 1L;
                String levelName = "중복된 난이도";

                LevelUpdateRequest request = new LevelUpdateRequest(levelId, levelName);

                // when & then
                mockMvc.perform(patch(URL_TEMPLATE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isUnauthorized());
            }

            @Test
            @DisplayName("관리자로 로그인하지 않은 경우 - 403 Forbidden 반환")
            public void shouldReturn403_whenUserIsNotAdmin() throws Exception {
                // given
                long levelId = 1L;
                String levelName = "중복된 난이도";

                LevelUpdateRequest request = new LevelUpdateRequest(levelId, levelName);

                // when & then
                mockMvc.perform(patch(URL_TEMPLATE)
                                .with(Util.getAuthentication(2L, ROLE_USER))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isForbidden());
            }
        }

    }

    @Nested
    @DisplayName("삭제 API 테스트")
    public class DeleteApiTest {

        @Nested
        @DisplayName("성공 테스트")
        public class SuccessCases {

            @Test
            @DisplayName("유효한 요청인 경우 - 200 OK 반환")
            public void shouldReturn200_whenRequestIsValid() throws Exception {
                // given
                long levelId = 1L;
                String levelName = "삭제된 난이도";
                LocalDateTime createdAt = LocalDateTime.now();
                LocalDateTime modifiedAt = null;

                when(levelService.deleteLevel(eq(levelId)))
                        .thenReturn(new LevelDto(levelId, levelName, createdAt, modifiedAt));

                // when & then
                mockMvc.perform(delete(URL_TEMPLATE + "/" + levelId)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN))
                                .param("levelId", String.valueOf(levelId)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.levelId").value(levelId))
                        .andExpect(jsonPath("$.levelName").value(levelName))
                        .andExpect(jsonPath("$.createdAt").value(Util.formatToIsoLocalDateTime(createdAt)))
                        .andExpect(jsonPath("$.modifiedAt").value(modifiedAt));

                verify(levelService).deleteLevel(eq(levelId));
            }
        }

        @Nested
        @DisplayName("실패 테스트")
        public class FailureCases {

            @Test
            @DisplayName("난이도 ID가 null인 경우 - 400 BAD_REQUEST 반환")
            public void shouldReturn400_whenLevelIdIsNull() throws Exception {
                // given
                Long levelId = null;

                // when & then
                mockMvc.perform(delete(URL_TEMPLATE + "/" + levelId)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN))
                                .param("levelId", String.valueOf(levelId)))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.name()))
                        .andExpect(jsonPath("$.message").value("For input string: \"null\""));
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "-1",
                    "0",
                    "9223372036854775807"
            })
            @DisplayName("존재하지 않는 난이도 ID를 사용하려는 경우 - 404 NOT_FOUND 반환")
            public void shouldReturn404_whenLevelIdDoesNotExist(long levelId) throws Exception {
                // given
                when(levelService.deleteLevel(eq(levelId)))
                        .thenThrow(new EntityNotFoundException(ERR_MSG_ENTITY_NOT_FOUND));

                // when & then
                mockMvc.perform(delete(URL_TEMPLATE + "/" + levelId)
                                .with(Util.getAuthentication(ADMIN_USER_ID, ROLE_ADMIN))
                                .param("levelId", String.valueOf(levelId)))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.name()))
                        .andExpect(jsonPath("$.message").value(ERR_MSG_ENTITY_NOT_FOUND));

                verify(levelService).deleteLevel(eq(levelId));
            }

            @Test
            @DisplayName("로그인하지 않은 경우 - 401 Unauthorized 반환")
            public void shouldReturn401_whenUserDoesNotSignIn() throws Exception {
                // given
                long levelId = 1L;

                // when & then
                mockMvc.perform(delete(URL_TEMPLATE + "/" + levelId)
                                .param("levelId", String.valueOf(levelId)))
                        .andExpect(status().isUnauthorized());
            }

            @Test
            @DisplayName("관리자로 로그인하지 않은 경우 - 403 Forbidden 반환")
            public void shouldReturn403_whenUserIsNotAdmin() throws Exception {
                // given
                long levelId = 1L;

                // when & then
                mockMvc.perform(delete(URL_TEMPLATE + "/" + levelId)
                                .with(Util.getAuthentication(2L, ROLE_USER))
                                .param("levelId", String.valueOf(levelId)))
                        .andExpect(status().isForbidden());
            }
        }

    }
}