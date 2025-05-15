package com.upstage.devup.user.account.controller;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.user.account.dto.UserAccountDto;
import com.upstage.devup.user.account.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @GetMapping
    public ResponseEntity<?> getUserAccount(@AuthenticationPrincipal AuthenticatedUser user) {
        UserAccountDto result = userAccountService.getUserAccount(user.getUserId());
        return ResponseEntity.ok(result);
    }
}
