package com.upstage.devup.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MyPageViewController {

    /**
     * 마이페이지 화면 불러오기
     *
     * @return 마이페이지 화면 파일 경로
     */
    @GetMapping
    public String getMyPageView() {
        return "user/mypage/mypage";
    }
}
