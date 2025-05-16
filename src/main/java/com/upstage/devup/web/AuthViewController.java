package com.upstage.devup.web;

import com.upstage.devup.auth.config.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthViewController {

    @GetMapping("/signup")
    public String getSignUpPage() {
        return "auth/signup";
    }

    @GetMapping("/signin")
    public String getSignInPage(@AuthenticationPrincipal AuthenticatedUser user) {
        if (user == null) {
            return "auth/signin";
        }

        return "redirect:/mypage";
    }
}
