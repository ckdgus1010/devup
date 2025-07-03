package com.upstage.devup.ai.controller;

import com.upstage.devup.ai.dto.AiAnswerCheckRequestDto;
import com.upstage.devup.ai.service.AiAnswerCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/answer")
public class AiAnswerCheckController {

    private final AiAnswerCheckService aiAnswerCheckService;

    @PostMapping
    public ResponseEntity<?> checkAnswer(@RequestBody AiAnswerCheckRequestDto request) {

        return ResponseEntity.ok(
                aiAnswerCheckService.checkUserAnswer(request)
        );
    }
}
