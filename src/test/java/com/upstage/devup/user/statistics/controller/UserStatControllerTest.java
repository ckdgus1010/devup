package com.upstage.devup.user.statistics.controller;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.user.statistics.dto.WrongNoteSummaryDto;
import com.upstage.devup.user.statistics.service.UserAnswerStatService;
import com.upstage.devup.user.statistics.service.UserWrongAnswerReadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

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
    private UserWrongAnswerReadService userWrongAnswerReadService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("오답 노트 목록 조회 성공")
    public void shouldGetWrongNote() throws Exception {
        // given
        Long userId = 1L;

        Integer pageNumber = 0;
        int pageSize = 10;

        WrongNoteSummaryDto dto = WrongNoteSummaryDto.builder()
                .userId(userId)
                .questionId(1L)
                .title("임시 제목")
                .category("임시 카테고리")
                .level("임시 난이도")
                .createdAt(LocalDateTime.now())
                .build();

        List<WrongNoteSummaryDto> items = List.of(dto);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<WrongNoteSummaryDto> mockPage = new PageImpl<>(items, pageable, items.size());

        when(userWrongAnswerReadService.getWrongNoteSummaries(eq(userId), eq(pageNumber)))
                .thenReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/stat/wrong")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .with(getAuthentication(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].userId").value(String.valueOf(userId)))
                .andExpect(jsonPath("$.content[0].questionId").value(String.valueOf(dto.getQuestionId())))
                .andExpect(jsonPath("$.content[0].title").value(String.valueOf(dto.getTitle())))
                .andExpect(jsonPath("$.content[0].category").value(String.valueOf(dto.getCategory())))
                .andExpect(jsonPath("$.content[0].level").value(String.valueOf(dto.getLevel())));
    }

    private RequestPostProcessor getAuthentication(Long userId) {
        AuthenticatedUser user = new AuthenticatedUser(userId);
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, null);
        return authentication(auth);
    }
}