package com.upstage.devup.answer.controller;

import com.upstage.devup.answer.domain.dto.AnswerDetailDto;
import com.upstage.devup.answer.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping("/{questionId}")
    public ResponseEntity<?> getAnswerByQuestionId(@PathVariable Long questionId) {
        AnswerDetailDto result = answerService.getAnswerByQuestionId(questionId);
        return ResponseEntity.ok(result);
    }
}
