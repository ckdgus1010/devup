package com.upstage.devup.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDto {

    private Long id;
    private String loginId;
    private String nickname;
    private String email;

}
