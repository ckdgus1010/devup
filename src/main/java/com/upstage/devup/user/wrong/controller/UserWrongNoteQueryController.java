package com.upstage.devup.user.wrong.controller;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.user.statistics.dto.WrongNoteSummaryDto;
import com.upstage.devup.user.wrong.service.UserWrongAnswerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wrong")
@RequiredArgsConstructor
public class UserWrongNoteQueryController {

    private final UserWrongAnswerQueryService userWrongAnswerQueryService;

    @GetMapping
    public ResponseEntity<?> getWrongNotes(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "0") Integer pageNumber
    ) {

        if (pageNumber < 0) {
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }

        Page<WrongNoteSummaryDto> result
                = userWrongAnswerQueryService.getWrongNoteSummaries(user.getUserId(), pageNumber);

        return ResponseEntity.ok(result);
    }
}
