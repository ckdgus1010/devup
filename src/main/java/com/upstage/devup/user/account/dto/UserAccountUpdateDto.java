package com.upstage.devup.user.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserAccountUpdateDto {

    private String type;
    private String nickname;
    private String email;

}
