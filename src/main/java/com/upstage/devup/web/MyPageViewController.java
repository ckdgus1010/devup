package com.upstage.devup.web;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.user.statistics.dto.UserAnswerStatDto;
import com.upstage.devup.user.statistics.dto.UserCategoryStatDto;
import com.upstage.devup.user.statistics.service.UserAnswerStatService;
import com.upstage.devup.user.statistics.service.UserWrongAnswerReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageViewController {

    private final UserAnswerStatService userAnswerStatService;
    private final UserWrongAnswerReadService userWrongAnswerReadService;

    /**
     * 마이페이지 화면 불러오기
     *
     * @return 마이페이지 화면 파일 경로
     */
    @GetMapping
    public String getMyPageView(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, Model model) {
        Long userId = authenticatedUser.getUserId();

        // 통계 정보 조회
        UserAnswerStatDto quizStat = userAnswerStatService.getUserAnswerStat(userId);
        UserCategoryStatDto categoryStat = userAnswerStatService.getUserCategoryStat(userId);

        model.addAttribute("quizStat", quizStat);
        model.addAttribute("categoryStat", categoryStat);

        return "user/mypage/mypage";
    }
}
