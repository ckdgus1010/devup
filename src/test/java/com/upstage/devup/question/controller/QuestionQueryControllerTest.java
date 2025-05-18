package com.upstage.devup.question.controller;

import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.question.dto.QuestionDetailDto;
import com.upstage.devup.question.service.QuestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Nested
@WebMvcTest(QuestionQueryController.class)
@Import(SecurityConfig.class)
@DisplayName("면접 질문 제목 검색 테스트")
class QuestionQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private QuestionService questionService;

    private static final String URI_TEMPLATE = "/api/questions/search";

    private QuestionDetailDto createSampleDto() {
        return QuestionDetailDto.builder()
                .id(1L)
                .title("OOP란?")
                .questionText("OOP가 무엇인지 설명하세요.")
                .category("JAVA")
                .level("하")
                .createdAt(LocalDateTime.now())
                .modifiedAt(null)
                .build();
    }

    @Nested
    @DisplayName("성공 케이스")
    public class SuccessCases {

        @Test
        @DisplayName("유효한 요청 데이터가 넘어온 경우")
        public void shouldReturnPages_whenRequestIsValid() throws Exception {
            // given
            String title = "란?";
            int pageNumber = 0;
            int pageSize = 10;
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

            QuestionDetailDto sample = createSampleDto();
            List<QuestionDetailDto> items = List.of(sample);
            Page<QuestionDetailDto> mockPage = new PageImpl<>(
                    items,
                    PageRequest.of(pageNumber, pageSize, sort),
                    items.size()
            );

            when(questionService.searchQuestionsByTitle(eq(title), eq(pageNumber)))
                    .thenReturn(mockPage);

            // when & then
            String expectedCreatedAt = sample.getCreatedAt()
                    .truncatedTo(ChronoUnit.SECONDS)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            mockMvc.perform(get(URI_TEMPLATE)
                            .param("title", title)
                            .param("pageNumber", String.valueOf(pageNumber)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.number").value(pageNumber))
                    .andExpect(jsonPath("$.first").value(true))
                    .andExpect(jsonPath("$.size").value(pageSize))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(sample.getId()))
                    .andExpect(jsonPath("$.content[0].title").value(sample.getTitle()))
                    .andExpect(jsonPath("$.content[0].questionText").value(sample.getQuestionText()))
                    .andExpect(jsonPath("$.content[0].category").value(sample.getCategory()))
                    .andExpect(jsonPath("$.content[0].level").value(sample.getLevel()))
                    .andExpect(jsonPath("$.content[0].createdAt").value(expectedCreatedAt))
                    .andExpect(jsonPath("$.content[0].modifiedAt").value(nullValue()));
        }

        @Test
        @DisplayName("pageNumber가 null로 전달된 경우, defaultValue인 0으로 처리된다")
        public void shouldReturnPage_whenPageNumberIsNull() throws Exception {
            // given
            String title = "검색어";
            int pageSize = 10;

            QuestionDetailDto sample = createSampleDto();
            List<QuestionDetailDto> items = List.of(sample);
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            Page<QuestionDetailDto> mockPage = new PageImpl<>(
                    items,
                    PageRequest.of(0, pageSize, sort),
                    items.size()
            );

            when(questionService.searchQuestionsByTitle(eq(title), eq(0)))
                    .thenReturn(mockPage);

            // when & then
            String expectedCreatedAt = sample.getCreatedAt()
                    .truncatedTo(ChronoUnit.SECONDS)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            mockMvc.perform(get(URI_TEMPLATE)
                            .param("title", title))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.number").value(0))
                    .andExpect(jsonPath("$.first").value(true))
                    .andExpect(jsonPath("$.size").value(pageSize))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(sample.getId()))
                    .andExpect(jsonPath("$.content[0].title").value(sample.getTitle()))
                    .andExpect(jsonPath("$.content[0].questionText").value(sample.getQuestionText()))
                    .andExpect(jsonPath("$.content[0].category").value(sample.getCategory()))
                    .andExpect(jsonPath("$.content[0].level").value(sample.getLevel()))
                    .andExpect(jsonPath("$.content[0].createdAt").value(expectedCreatedAt))
                    .andExpect(jsonPath("$.content[0].modifiedAt").value(nullValue()));
        }
    }

    @Nested
    @DisplayName("실패 케이스 - 유효하지 않은 검색어")
    public class FailureCasesUsingUnavailableTitle {

        @Test
        @DisplayName("검색어가 null인 경우")
        public void shouldReturnEmptyPage_whenTitleIsNull() throws Exception {
            // given
            int pageNumber = 0;

            // when & then
            mockMvc.perform(get(URI_TEMPLATE)
                            .param("pageNumber", String.valueOf(pageNumber)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("검색어를 입력하지 않은 경우")
        public void shouldReturnEmptyPage_whenTitleIsEmpty() throws Exception {
            // given
            String title = "";
            int pageNumber = 0;
            int pageSize = 10;
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

            Page<QuestionDetailDto> mockPage = new PageImpl<>(
                    List.of(),
                    PageRequest.of(pageNumber, pageSize, sort),
                    0
            );

            when(questionService.searchQuestionsByTitle(eq(title), eq(pageNumber)))
                    .thenReturn(mockPage);

            // when & then
            mockMvc.perform(get(URI_TEMPLATE)
                            .param("title", title)
                            .param("pageNumber", String.valueOf(pageNumber)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.empty").value(true))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("totalPages").value(0))
                    .andExpect(jsonPath("$.totalElements").value(0))
                    .andExpect(jsonPath("$.first").value(true))
                    .andExpect(jsonPath("$.number").value(pageNumber))
                    .andExpect(jsonPath("$.size").value(pageSize));
        }

        @Test
        @DisplayName("검색어 길이가 2보다 작은 경우")
        public void shouldReturnEmptyPage_whenTitleLengthIsLessThan2() throws Exception {
            // given
            String title = "일";
            int pageNumber = 0;
            int pageSize = 10;
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

            Page<QuestionDetailDto> mockPage = new PageImpl<>(
                    List.of(),
                    PageRequest.of(pageNumber, pageSize, sort),
                    0
            );

            when(questionService.searchQuestionsByTitle(eq(title), eq(pageNumber)))
                    .thenReturn(mockPage);

            // when & then
            mockMvc.perform(get(URI_TEMPLATE)
                            .param("title", title)
                            .param("pageNumber", String.valueOf(pageNumber)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.empty").value(true))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("totalPages").value(0))
                    .andExpect(jsonPath("$.totalElements").value(0))
                    .andExpect(jsonPath("$.first").value(true))
                    .andExpect(jsonPath("$.number").value(pageNumber))
                    .andExpect(jsonPath("$.size").value(pageSize));
        }
    }

    @Nested
    @DisplayName("실패 케이스 - 유효하지 않은 페이지 번호")
    public class FailureCasesUsingUnavailablePageNumber {

        @Test
        @DisplayName("페이지 번호가 음수인 경우")
        public void shouldReturn400_whenPageNumberIsNegative() throws Exception {
            // given
            String title = "검색어";
            Integer pageNumber = -1;

            // when & then
            mockMvc.perform(get(URI_TEMPLATE)
                            .param("title", title)
                            .param("pageNumber", String.valueOf(pageNumber)))
                    .andExpect(status().isBadRequest());
        }
    }
}