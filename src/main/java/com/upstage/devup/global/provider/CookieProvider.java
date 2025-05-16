package com.upstage.devup.global.provider;

import com.upstage.devup.global.properties.CookieProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieProvider {

    private final CookieProperties cookieProperties;

    private static final String ACCESS_TOKEN_NAME = "accessToken";
    private static final String EMPTY_TOKEN = "";
    private static final String SAME_SITE = "Strict";
    private static final String COOKIE_PATH = "/";

    /**
     * JWT 토큰을 담은 accessToken 쿠키를 생성
     *
     * @param token JWT 토큰
     * @return 생성된 쿠키
     */
    public ResponseCookie createAccessTokenCookie(String token) {
        return createCookie(token, cookieProperties.getAccessTokenMaxAge());
    }

    /**
     * accessToken을 삭제하는 쿠키를 생성
     *
     * @return 생성된 쿠키
     */
    public ResponseCookie removeAccessTokenCookie() {
        return createCookie(EMPTY_TOKEN, 0);
    }

    /**
     * 쿠키 생성
     *
     * @param token  쿠키에 담을 토큰
     * @param maxAge 쿠키 유효 기간(초 단위)
     * @return
     */
    private ResponseCookie createCookie(String token, long maxAge) {
        return ResponseCookie
                .from(ACCESS_TOKEN_NAME, token)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .maxAge(maxAge)
                .sameSite(SAME_SITE)    // CSRF: 방지: 같은 사이트 요청에서만 쿠키 전송
                .path(COOKIE_PATH)
                .build();
    }
}
