package com.upstage.devup.ai.controller;

import com.upstage.devup.ai.dto.AiAnswerCheckRequestDto;
import com.upstage.devup.ai.dto.AiAnswerCheckResponseDto;
import com.upstage.devup.ai.service.AiAnswerCheckService;
import com.upstage.devup.auth.config.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/answers")
public class AiAnswerCheckController {

    private final AiAnswerCheckService aiAnswerCheckService;

    @PostMapping
    public ResponseEntity<AiAnswerCheckResponseDto> checkAnswer(
            @RequestBody @Valid AiAnswerCheckRequestDto request,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {

        return ResponseEntity.ok(
                aiAnswerCheckService.checkUserAnswer(user.userId(), request)
        );
    }
}
