package com.upstage.devup.auth.service;

import com.upstage.devup.auth.domain.dto.SignUpRequestDto;
import com.upstage.devup.auth.domain.dto.SignUpResponseDto;
import com.upstage.devup.auth.domain.entity.User;
import com.upstage.devup.auth.domain.mapper.UserMapper;
import com.upstage.devup.auth.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * 회원가입
     *
     * @param request 회원가입 요청 정보
     * @return 회원가입 결과
     */
    public SignUpResponseDto signUp(SignUpRequestDto request) {
        if (request == null) {
            return null;
        }

        // 중복 검사
        if (isLoginIdInUse(request.getLoginId())) {
            return null;
        }

        if (isNicknameInUse(request.getNickname())) {
            return null;
        }

        if (isEmailInUse(request.getEmail())) {
            return null;
        }

        User savedEntity = userRepository.save(userMapper.toEntity(request));

        return userMapper.toSignUpResponseDto(savedEntity);
    }

    /**
     * 로그인 ID가 사용 중인지 확인
     *
     * @param loginId 확인할 로그인 ID
     * @return true: 이미 사용 중, false: 사용 가능
     */
    public boolean isLoginIdInUse(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    /**
     * 닉네임이 사용 중인지 확인
     *
     * @param nickname 확인할 닉네임
     * @return true: 이미 사용 중, false: 사용 가능
     */
    public boolean isNicknameInUse(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /**
     * 이메일이 사용 중인지 확인
     *
     * @param email 확인할 이메일
     * @return true: 이미 사용 중, false: 사용 가능
     */
    public boolean isEmailInUse(String email) {
        return userRepository.existsByEmail(email);
    }
}
