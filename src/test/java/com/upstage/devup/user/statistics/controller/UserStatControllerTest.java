package com.upstage.devup.user.statistics.controller;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.user.statistics.domain.dto.UserSolvedQuestionDto;
import com.upstage.devup.user.statistics.service.UserAnswerStatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserStatController.class)
@Import(SecurityConfig.class)
class UserStatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserAnswerStatService userAnswerStatService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    public void shouldGetSolvedQuestions() throws Exception {
        // given
        Long userId = 1L;

        Integer pageNumber = 0;
        int pageSize = 10;

        UserSolvedQuestionDto dto = UserSolvedQuestionDto.builder()
                .id(1L)
                .questionId(1L)
                .questionTitle("제목")
                .category("카테고리")
                .level("난이도")
                .firstSolvedAt(LocalDateTime.now())
                .lastSolvedAt(null)
                .isCorrect(true)
                .build();

        List<UserSolvedQuestionDto> items = List.of(dto);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<UserSolvedQuestionDto> mockPage = new PageImpl<>(items, pageable, items.size());

        when(userAnswerStatService.getUserSolvedQuestions(eq(userId), eq(pageNumber)))
                .thenReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/stat/history")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                new AuthenticatedUser(userId), null, null
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(dto.getId()))
                .andExpect(jsonPath("$.content[0].questionId").value(dto.getQuestionId()))
                .andExpect(jsonPath("$.content[0].questionTitle").value(dto.getQuestionTitle()))
                .andExpect(jsonPath("$.content[0].category").value(dto.getCategory()))
                .andExpect(jsonPath("$.content[0].level").value(dto.getLevel()))
                .andExpect(jsonPath("$.content[0].firstSolvedAt").value(String.valueOf(dto.getFirstSolvedAt())))
                .andExpect(jsonPath("$.content[0].lastSolvedAt").value(dto.getLastSolvedAt()))
                .andExpect(jsonPath("$.content[0].correct").value(String.valueOf(dto.isCorrect())))
                .andExpect(jsonPath("$.totalElements").value(String.valueOf(items.size())));
    }
}