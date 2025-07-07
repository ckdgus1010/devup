package com.upstage.devup.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upstage.devup.Util;
import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.category.dto.CategoryAddRequest;
import com.upstage.devup.category.dto.CategoryDto;
import com.upstage.devup.category.dto.CategoryUpdateRequest;
import com.upstage.devup.category.service.CategoryService;
import com.upstage.devup.global.exception.DuplicatedResourceException;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.global.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {CategoryController.class, GlobalExceptionHandler.class})
@Import(SecurityConfig.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CategoryService categoryService;

    private static final String URL_TEMPLATE = "/api/admin/category";
    private static final long userId = 1L;
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Nested
    @DisplayName("카테고리 등록 테스트")
    public class RegistrationTest {

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("유효한 요청이 온 경우")
            public void shouldReturn200_whenRequestIsValid() throws Exception {
                // given
                long categoryId = 10L;
                String categoryName = "등록할 카테고리";
                String color = "#123456";
                CategoryAddRequest request = new CategoryAddRequest(categoryName, color);
                CategoryDto mockResult = new CategoryDto(categoryId, categoryName, color);

                when(categoryService.addCategory(eq(request)))
                        .thenReturn(mockResult);

                // when & then
                mockMvc.perform(post(URL_TEMPLATE)
                                .with(Util.getAuthentication(userId, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id").value(mockResult.id()))
                        .andExpect(jsonPath("$.categoryName").value(mockResult.categoryName()))
                        .andExpect(jsonPath("$.color").value(mockResult.color()));

                verify(categoryService).addCategory(eq(request));
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @Test
            @DisplayName("카테고리 이름 누락 - 400 Bad Request 응답")
            public void shouldReturn400_whenCategoryNameIsBlank() throws Exception {
                // given
                String categoryName = "";
                String color = "#123456";
                CategoryAddRequest request = new CategoryAddRequest(categoryName, color);

                String code = "VALIDATION_FAILED";
                String errorMessage = "카테고리를 입력해 주세요.";

                // when & then
                mockMvc.perform(post(URL_TEMPLATE)
                                .with(Util.getAuthentication(userId, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(code))
                        .andExpect(jsonPath("$.message").value(errorMessage));
            }

            @Test
            @DisplayName("색상 누락 - 400 Bad Request 응답")
            public void shouldReturn404_whenColorIsBlank() throws Exception {
                // given
                String categoryName = "등록할 카테고리";
                String color = "";
                CategoryAddRequest request = new CategoryAddRequest(categoryName, color);

                String code = "VALIDATION_FAILED";
                String errorMessage = "색상을 입력해 주세요.";

                // when & then
                mockMvc.perform(post(URL_TEMPLATE)
                                .with(Util.getAuthentication(userId, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(code))
                        .andExpect(jsonPath("$.message").value(errorMessage));
            }

            @Test
            @DisplayName("이미 존재하는 카테고리 이름을 사용하는 경우 - 409 Conflict 응답")
            public void shouldReturn409_whenCategoryNameIsInUse() throws Exception {
                // given
                String categoryName = "중복된 카테고리";
                String color = "#123456";
                CategoryAddRequest request = new CategoryAddRequest(categoryName, color);

                String code = "CONFLICT";
                String errorMessage = "이미 존재하는 카테고리입니다.";

                when(categoryService.addCategory(eq(request)))
                        .thenThrow(new DuplicatedResourceException(errorMessage));

                // when & then
                mockMvc.perform(post(URL_TEMPLATE)
                                .with(Util.getAuthentication(userId, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isConflict())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(code))
                        .andExpect(jsonPath("$.message").value(errorMessage));

                verify(categoryService).addCategory(eq(request));
            }
        }
    }

    @Nested
    @DisplayName("카테고리 수정 테스트")
    public class UpdateTest {

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("유효한 요청이 온 경우")
            public void shouldReturn200_whenRequestIsValid() throws Exception {
                // given
                long categoryId = 1L;
                String categoryName = "수정할 카테고리";
                String color = "#123456";

                CategoryUpdateRequest request = new CategoryUpdateRequest(categoryId, categoryName, color);
                CategoryDto mockResult = new CategoryDto(categoryId, categoryName, color);

                when(categoryService.updateCategory(eq(request)))
                        .thenReturn(mockResult);

                // when & then
                mockMvc.perform(patch(URL_TEMPLATE)
                                .with(Util.getAuthentication(userId, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id").value(mockResult.id()))
                        .andExpect(jsonPath("$.categoryName").value(mockResult.categoryName()))
                        .andExpect(jsonPath("$.color").value(mockResult.color()));

                verify(categoryService).updateCategory(eq(request));
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @Test
            @DisplayName("카테고리 ID 누락 - 400 Bad Request 응답")
            public void shouldReturn400_whenCategoryIdIsBlank() throws Exception {
                // given
                Long categoryId = null;
                String categoryName = "수정할 카테고리";
                String color = "#123456";

                CategoryUpdateRequest request = new CategoryUpdateRequest(categoryId, categoryName, color);

                String code = "VALIDATION_FAILED";
                String errorMessage = "변경할 카테고리를 선택해 주세요.";

                // when & then
                mockMvc.perform(patch(URL_TEMPLATE)
                                .with(Util.getAuthentication(userId, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(code))
                        .andExpect(jsonPath("$.message").value(errorMessage));
            }

            @Test
            @DisplayName("카테고리 이름 누락 - 400 Bad Request 응답")
            public void shouldReturn400_whenCategoryNameIsBlank() throws Exception {
                // given
                Long categoryId = 1L;
                String categoryName = "";
                String color = "#123456";
                CategoryUpdateRequest request = new CategoryUpdateRequest(categoryId, categoryName, color);

                String code = "VALIDATION_FAILED";
                String errorMessage = "카테고리를 입력해 주세요.";

                // when & then
                mockMvc.perform(patch(URL_TEMPLATE)
                                .with(Util.getAuthentication(userId, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(code))
                        .andExpect(jsonPath("$.message").value(errorMessage));
            }

            @Test
            @DisplayName("색상 누락 - 400 Bad Request 응답")
            public void shouldReturn404_whenColorIsBlank() throws Exception {
                // given
                Long categoryId = 1L;
                String categoryName = "수정할 카테고리";
                String color = "";
                CategoryUpdateRequest request = new CategoryUpdateRequest(categoryId, categoryName, color);

                String code = "VALIDATION_FAILED";
                String errorMessage = "색상을 입력해 주세요.";

                // when & then
                mockMvc.perform(patch(URL_TEMPLATE)
                                .with(Util.getAuthentication(userId, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(code))
                        .andExpect(jsonPath("$.message").value(errorMessage));
            }

            @Test
            @DisplayName("이미 존재하는 카테고리 이름으로 수정하려는 경우 - 409 Conflict 응답")
            public void shouldReturn409_whenCategoryNameIsInUse() throws Exception {
                // given
                Long categoryId = 1L;
                String categoryName = "중복된 카테고리";
                String color = "#123456";
                CategoryUpdateRequest request = new CategoryUpdateRequest(categoryId, categoryName, color);

                String code = "CONFLICT";
                String errorMessage = "이미 존재하는 카테고리입니다.";

                when(categoryService.updateCategory(eq(request)))
                        .thenThrow(new DuplicatedResourceException(errorMessage));

                // when & then
                mockMvc.perform(patch(URL_TEMPLATE)
                                .with(Util.getAuthentication(userId, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isConflict())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(code))
                        .andExpect(jsonPath("$.message").value(errorMessage));

                verify(categoryService).updateCategory(eq(request));
            }

            @Test
            @DisplayName("존재하지 않은 카테고리를 수정하려는 경우 - 404 Not Found 응답")
            public void shouldReturn404_whenCategoryDoesNotExist() throws Exception {
                // given
                Long categoryId = Long.MAX_VALUE;
                String categoryName = "수정할 카테고리";
                String color = "#123456";
                CategoryUpdateRequest request = new CategoryUpdateRequest(categoryId, categoryName, color);

                String code = "NOT_FOUND";
                String errorMessage = "존재하지 않는 카테고리입니다.";

                when(categoryService.updateCategory(eq(request)))
                        .thenThrow(new EntityNotFoundException(errorMessage));

                // when & then
                mockMvc.perform(patch(URL_TEMPLATE)
                                .with(Util.getAuthentication(userId, ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(code))
                        .andExpect(jsonPath("$.message").value(errorMessage));

                verify(categoryService).updateCategory(eq(request));
            }
        }
    }

    @Nested
    @DisplayName("카테고리 삭제 테스트")
    public class DeletionTest {

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("외래키로 사용되지 않는 카테고리를 삭제하는 경우")
            public void shouldReturn200_whenCategoryIsNotUsedByForeignKey() throws Exception {
                // given
                long categoryId = 1L;
                String categoryName = "삭제할 카테고리";
                String color = "#123456";

                when(categoryService.deleteCategory(eq(categoryId)))
                        .thenReturn(new CategoryDto(categoryId, categoryName, color));

                // when & then
                mockMvc.perform(delete(URL_TEMPLATE + "/" + categoryId)
                                .with(Util.getAuthentication(userId, ROLE_ADMIN)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id").value(categoryId))
                        .andExpect(jsonPath("$.categoryName").value(categoryName))
                        .andExpect(jsonPath("$.color").value(color));

                verify(categoryService).deleteCategory(eq(categoryId));
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @Test
            @DisplayName("외래키로 사용되는 카테고리를 삭제하는 경우")
            public void shouldReturn409_whenCategoryIsUsedByForeignKey() throws Exception {
                // given
                long categoryId = 2L;
                String errorMessage = "삭제할 수 없습니다. 다른 데이터에서 이 항목을 사용 중입니다.";

                when(categoryService.deleteCategory(eq(categoryId)))
                        .thenThrow(
                                new DataIntegrityViolationException(errorMessage)
                        );

                // when & then
                mockMvc.perform(delete(URL_TEMPLATE + "/" + categoryId)
                                .with(Util.getAuthentication(userId, ROLE_ADMIN)))
                        .andExpect(status().isConflict())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(HttpStatus.CONFLICT.name()))
                        .andExpect(jsonPath("$.message").value(errorMessage));

                verify(categoryService).deleteCategory(eq(categoryId));
            }

            @Test
            @DisplayName("등록되지 않은 카테고리를 삭제하는 경우")
            public void shouldReturn404_whenCategoryDoesNotExist() throws Exception {
                // given
                long categoryId = Long.MAX_VALUE;
                String errorMessage = "존재하지 않는 카테고리입니다.";

                when(categoryService.deleteCategory(eq(categoryId)))
                        .thenThrow(
                                new EntityNotFoundException(errorMessage)
                        );

                // when & then
                mockMvc.perform(delete(URL_TEMPLATE + "/" + categoryId)
                                .with(Util.getAuthentication(userId, ROLE_ADMIN)))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.name()))
                        .andExpect(jsonPath("$.message").value(errorMessage));

                verify(categoryService).deleteCategory(eq(categoryId));
            }
        }
    }
}