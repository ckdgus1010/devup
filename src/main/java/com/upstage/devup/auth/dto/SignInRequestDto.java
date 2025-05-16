package com.upstage.devup.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequestDto {

    @NotBlank(message = "아이디는 필수 항목입니다.")
    private String loginId;


    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    private String password;

}
