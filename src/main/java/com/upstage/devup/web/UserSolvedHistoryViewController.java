package com.upstage.devup.web;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.user.history.dto.UserSolvedHistoryDetailDto;
import com.upstage.devup.user.history.service.UserSolvedHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/history")
@RequiredArgsConstructor
public class UserSolvedHistoryViewController {

    private final UserSolvedHistoryService userSolvedHistoryService;

    @GetMapping("/{userAnswerId}")
    public String getUserSolvedHistoryDetailView(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long userAnswerId,
            Model model
            ) {
        UserSolvedHistoryDetailDto result = userSolvedHistoryService.getUserSolvedHistoryDetail(userAnswerId);

        model.addAttribute("history", result);
        return "user/history/user-solved-history-detail";
    }
}
