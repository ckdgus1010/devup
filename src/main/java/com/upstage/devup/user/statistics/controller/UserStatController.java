package com.upstage.devup.user.statistics.controller;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.user.statistics.dto.UserSolvedQuestionDto;
import com.upstage.devup.user.statistics.dto.WrongNoteSummaryDto;
import com.upstage.devup.user.statistics.service.UserAnswerStatService;
import com.upstage.devup.user.statistics.service.UserWrongAnswerReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stat")
@RequiredArgsConstructor
public class UserStatController {

    private final UserAnswerStatService userAnswerStatService;
    private final UserWrongAnswerReadService userWrongAnswerReadService;

    @GetMapping("/history")
    public ResponseEntity<?> getSolvedQuestions(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam Integer pageNumber) {

        Page<UserSolvedQuestionDto> solvedQuestions
                = userAnswerStatService.getUserSolvedQuestions(user.getUserId(), pageNumber);

        return ResponseEntity.ok(solvedQuestions);
    }

    @GetMapping("/wrong")
    public ResponseEntity<?> getWrongNotes(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam Integer pageNumber) {

        Page<WrongNoteSummaryDto> summaries
                = userWrongAnswerReadService.getWrongNoteSummaries(user.getUserId(), pageNumber);

        return ResponseEntity.ok(summaries);
    }
}
