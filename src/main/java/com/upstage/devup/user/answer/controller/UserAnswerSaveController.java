package com.upstage.devup.user.answer.controller;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.user.answer.dto.UserAnswerDetailDto;
import com.upstage.devup.user.answer.dto.UserAnswerSaveRequest;
import com.upstage.devup.user.answer.service.UserAnswerSaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user/answer")
@RequiredArgsConstructor
public class UserAnswerSaveController {

    private final UserAnswerSaveService userAnswerSaveService;

    @PostMapping
    public ResponseEntity<?> saveUserAnswer(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody @Valid UserAnswerSaveRequest request) {
        UserAnswerDetailDto result = userAnswerSaveService.saveUserAnswer(user.getUserId(), request);
        return ResponseEntity.ok(result);
    }
}
