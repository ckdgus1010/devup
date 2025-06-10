package com.upstage.devup.category.controller;

import com.upstage.devup.Util;
import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.category.dto.CategoryDto;
import com.upstage.devup.category.service.CategoryService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {CategoryController.class, GlobalExceptionHandler.class})
@Import(SecurityConfig.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CategoryService categoryService;

    private static final String URL_TEMPLATE = "/api/admin/category/";
    private static final long userId = 1L;
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

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
                mockMvc.perform(delete(URL_TEMPLATE + categoryId)
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
                mockMvc.perform(delete(URL_TEMPLATE + categoryId)
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
                mockMvc.perform(delete(URL_TEMPLATE + categoryId)
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