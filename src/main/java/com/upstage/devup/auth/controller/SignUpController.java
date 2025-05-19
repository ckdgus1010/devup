package com.upstage.devup.auth.controller;

import com.upstage.devup.auth.dto.SignUpRequestDto;
import com.upstage.devup.auth.dto.SignUpResponseDto;
import com.upstage.devup.auth.service.UserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/signup")
@RequiredArgsConstructor
public class SignUpController {

    private final UserAuthService userAuthService;

    @PostMapping
    public ResponseEntity<SignUpResponseDto> signUp(@RequestBody @Valid SignUpRequestDto request) {
        SignUpResponseDto result = userAuthService.signUp(request);
        return ResponseEntity.ok(result);
    }
}
