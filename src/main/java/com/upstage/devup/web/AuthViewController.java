package com.upstage.devup.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthViewController {

    @GetMapping("/signup")
    public String getSignupPage() {
        return "auth/signup";
    }
}
