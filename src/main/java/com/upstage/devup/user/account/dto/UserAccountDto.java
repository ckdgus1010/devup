package com.upstage.devup.user.account.dto;

import com.upstage.devup.auth.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserAccountDto {

    private Long userId;
    private String loginId;
    private String nickname;
    private String email;

    public static UserAccountDto of(User entity) {
        return UserAccountDto.builder()
                .userId(entity.getId())
                .loginId(entity.getLoginId())
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .build();
    }

}
