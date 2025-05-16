package com.upstage.devup.auth.controller;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.auth.dto.LogoutResponseDto;
import com.upstage.devup.global.exception.UnauthenticatedException;
import com.upstage.devup.global.provider.CookieProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/logout")
@Slf4j
@RequiredArgsConstructor
public class LogoutController {

    private final CookieProvider cookieProvider;
    private static final LogoutResponseDto LOGOUT_RESPONSE_DTO = new LogoutResponseDto("/");

    @PostMapping
    public ResponseEntity<?> logout(@AuthenticationPrincipal AuthenticatedUser user, HttpServletResponse response) {
        log.info("로그아웃 요청 userId = {}", user.getUserId());

        if (user.getUserId() == null || user.getUserId() <= 0L) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        // 쿠키 삭제 명령
        ResponseCookie cookie = cookieProvider.removeAccessTokenCookie();
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(LOGOUT_RESPONSE_DTO);
    }
}
