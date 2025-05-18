package com.upstage.devup.question.controller;

import com.upstage.devup.question.dto.QuestionDetailDto;
import com.upstage.devup.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/questions/search")
@RequiredArgsConstructor
public class QuestionQueryController {

    private final QuestionService questionService;

    /**
     * 제목으로 문제 검색
     *
     * @param title 검색할 제목
     * @param pageNumber 페이지 번호
     * @return 조회된 결과
     */
    @GetMapping
    public ResponseEntity<?> searchQuestionsByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") Integer pageNumber
    ) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }

        Page<QuestionDetailDto> result = questionService.searchQuestionsByTitle(title, pageNumber);
        return ResponseEntity.ok(result);
    }
}
