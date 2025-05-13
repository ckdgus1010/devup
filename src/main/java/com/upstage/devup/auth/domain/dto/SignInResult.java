package com.upstage.devup.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignInResult {

    private String token;

    private Long userId;
    private String loginId;
    private String nickname;

    private String redirectUrl;
}
