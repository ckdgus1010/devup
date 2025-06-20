package com.upstage.devup.auth.service;

import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.auth.dto.SignInRequestDto;
import com.upstage.devup.auth.dto.SignInResult;
import com.upstage.devup.auth.dto.SignUpRequestDto;
import com.upstage.devup.auth.dto.SignUpResponseDto;
import com.upstage.devup.global.entity.User;
import com.upstage.devup.auth.mapper.UserMapper;
import com.upstage.devup.global.exception.InvalidLoginException;
import com.upstage.devup.auth.respository.UserAuthRepository;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.global.exception.ValueAlreadyInUseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserAuthRepository userAuthRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인
     *
     * @param request 로그인 요청 정보
     * @return JWT 토큰
     * @throws IllegalArgumentException 요청 데이터가 null일 때 발생
     * @throws InvalidLoginException 아이디 또는 비밀번호가 일치하지 않는 경우 발생
     */
    public SignInResult signIn(SignInRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("유효하지 않는 요청입니다.");
        }

        if (request.getPassword() == null) {
            throw new InvalidLoginException("아이디 또는 비밀번호를 확인해주세요.");
        }

        User user = userAuthRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new InvalidLoginException("아이디 또는 비밀번호를 확인해주세요."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidLoginException("아이디 또는 비밀번호를 확인해주세요.");
        }

        // JWT 발급
        String token = jwtTokenProvider.generateToken(user.getId());

        return SignInResult.builder()
                .token(token)
                .userId(user.getId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .redirectUrl("/mypage")
                .build();
    }

    /**
     * 회원가입
     *
     * @param request 회원가입 요청 정보
     * @return 회원가입 결과
     * @throws IllegalArgumentException 요청 데이터가 null일 때 발생
     * @throws ValueAlreadyInUseException 중복 검사 시 이미 사용 중인 값일 때 발생
     */
    public SignUpResponseDto signUp(SignUpRequestDto request) {
        // 중복 검사
        if (isLoginIdInUse(request.getLoginId())) {
            throw new ValueAlreadyInUseException("이미 사용 중인 아이디입니다.");
        }

        if (isNicknameInUse(request.getNickname())) {
            throw new ValueAlreadyInUseException("이미 사용 중인 닉네임입니다.");
        }

        if (isEmailInUse(request.getEmail())) {
            throw new ValueAlreadyInUseException("이미 사용 중인 이메일입니다.");
        }

        User savedEntity = userAuthRepository.save(userMapper.toEntity(request));

        return userMapper.toSignUpResponseDto(savedEntity);
    }

    /**
     * 유저 ID가 사용 중인지 확인
     *
     * @param userId 확인할 유저 ID
     * @return true: 사용 중인 ID, false: 사용하지 않는 ID
     */
    private boolean isUserIdInUse(Long userId) {
        if (userId == null) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        return userAuthRepository.existsById(userId);
    }

    /**
     * 로그인 ID가 사용 중인지 확인
     *
     * @param loginId 확인할 로그인 ID
     * @return true: 이미 사용 중, false: 사용 가능
     */
    private boolean isLoginIdInUse(String loginId) {
        return userAuthRepository.existsByLoginId(loginId);
    }

    /**
     * 닉네임이 사용 중인지 확인
     *
     * @param nickname 확인할 닉네임
     * @return true: 이미 사용 중, false: 사용 가능
     */
    private boolean isNicknameInUse(String nickname) {
        return userAuthRepository.existsByNickname(nickname);
    }

    /**
     * 이메일이 사용 중인지 확인
     *
     * @param email 확인할 이메일
     * @return true: 이미 사용 중, false: 사용 가능
     */
    private boolean isEmailInUse(String email) {
        return userAuthRepository.existsByEmail(email);
    }
}
