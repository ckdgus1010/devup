package com.upstage.devup.auth.controller;

import com.upstage.devup.auth.domain.dto.SignInRequestDto;
import com.upstage.devup.auth.domain.dto.SignInResponseDto;
import com.upstage.devup.auth.domain.dto.SignInResult;
import com.upstage.devup.auth.service.UserAuthService;
import com.upstage.devup.global.provider.CookieProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/signin")
@RequiredArgsConstructor
public class SignInController {

    private final UserAuthService userAuthService;
    private final CookieProvider cookieProvider;

    /**
     * 로그인
     *
     * @param request 로그인 요청 데이터
     * @return 로그인 결과
     */
    @PostMapping
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequestDto request, HttpServletResponse response) {
        SignInResult result = userAuthService.signIn(request);

        // 쿠키 설정
        ResponseCookie cookie = cookieProvider.createAccessTokenCookie(result.getToken());
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new SignInResponseDto(result));
    }
}
