package com.upstage.devup.auth.mapper;

import com.upstage.devup.auth.dto.SignUpRequestDto;
import com.upstage.devup.auth.dto.SignUpResponseDto;
import com.upstage.devup.global.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toEntity(SignUpRequestDto request) {

        return User.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .email(request.getEmail())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public SignUpResponseDto toSignUpResponseDto(User entity) {
        return SignUpResponseDto.builder()
                .id(entity.getId())
                .loginId(entity.getLoginId())
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .build();
    }
}
