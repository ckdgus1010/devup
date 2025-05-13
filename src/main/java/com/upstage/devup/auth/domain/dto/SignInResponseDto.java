package com.upstage.devup.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignInResponseDto {

    private final Long userId;
    private final String loginId;
    private final String nickname;

    private final String redirectUrl;

    public SignInResponseDto(SignInResult result) {
        this.userId = result.getUserId();
        this.loginId = result.getLoginId();
        this.nickname = result.getNickname();

        this.redirectUrl = result.getRedirectUrl();
    }
}
