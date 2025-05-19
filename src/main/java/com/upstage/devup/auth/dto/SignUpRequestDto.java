package com.upstage.devup.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SignUpRequestDto {

    @NotBlank(message = "아이디는 필수 항목입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 항목입니다.")
    private String nickname;

    @NotBlank(message = "이메일 형식이 아닙니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

}
