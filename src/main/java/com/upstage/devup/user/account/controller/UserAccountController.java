package com.upstage.devup.user.account.controller;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.user.account.dto.UserAccountDto;
import com.upstage.devup.user.account.dto.UserAccountUpdateDto;
import com.upstage.devup.user.account.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @GetMapping
    public ResponseEntity<?> getUserAccount(@AuthenticationPrincipal AuthenticatedUser user) {
        UserAccountDto result = userAccountService.getUserAccount(user.userId());
        return ResponseEntity.ok(result);
    }

    @PatchMapping
    public ResponseEntity<?> updateUserAccount(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody UserAccountUpdateDto userAccountUpdateDto) {

        System.out.println("UserAccountController.updateUserAccount");
        UserAccountDto result = userAccountService.updateUserAccount(user.userId(), userAccountUpdateDto);
        return ResponseEntity.ok(result);
    }
}
