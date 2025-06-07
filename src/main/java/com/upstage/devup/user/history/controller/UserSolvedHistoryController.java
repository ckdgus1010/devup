package com.upstage.devup.user.history.controller;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.user.history.service.UserSolvedHistoryService;
import com.upstage.devup.user.history.dto.UserSolvedQuestionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user/history")
@RequiredArgsConstructor
public class UserSolvedHistoryController {

    private final UserSolvedHistoryService userSolvedHistoryService;

    @GetMapping
    public ResponseEntity<?> getSolvedHistories(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam Integer pageNumber) {

        Page<UserSolvedQuestionDto> results
                = userSolvedHistoryService.getUserSolvedQuestions(user.userId(), pageNumber);

        return ResponseEntity.ok(results);
    }
}
