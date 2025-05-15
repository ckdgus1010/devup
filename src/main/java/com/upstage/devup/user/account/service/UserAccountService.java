package com.upstage.devup.user.account.service;

import com.upstage.devup.auth.domain.entity.User;
import com.upstage.devup.auth.respository.UserRepository;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.global.exception.ValueAlreadyInUseException;
import com.upstage.devup.user.account.dto.UserAccountDto;
import com.upstage.devup.user.account.dto.UserAccountUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAccountService {

    private final UserRepository userRepository;

    /**
     * 사용자 정보 불러오기
     *
     * @param userId 사용자 ID
     * @return 조회된 사용자 정보
     * @throws EntityNotFoundException 회원 정보를 찾을 수 없을 때 발생
     */
    public UserAccountDto getUserAccount(Long userId) {
        if (userId == null) {
            throw new EntityNotFoundException("회원 정보를 찾을 수 없습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        return UserAccountDto.of(user);
    }

    public UserAccountDto updateUserAccount(Long userId, UserAccountUpdateDto request) {
        if (userId == null) {
            throw new EntityNotFoundException("회원 정보를 찾을 수 없습니다.");
        }

        if (request == null) {
            throw new IllegalArgumentException("유효한 데이터가 아닙니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        switch (request.getType()) {
            case "nickname":
                updateNickname(user, request.getNickname());
                break;
            case "email":
                updateEmail(user, request.getEmail());
                break;
        }

        User saved = userRepository.save(user);
        return UserAccountDto.of(saved);
    }

    private void updateNickname(User user, String newNickname) {
        if (newNickname == null || newNickname.isBlank()) {
            throw new IllegalArgumentException("유효하지 않은 닉네임입니다.");
        }

        if (user.getNickname().equals(newNickname)) {
            throw new ValueAlreadyInUseException("이미 사용중인 닉네임입니다.");
        }

        if (userRepository.existsByNickname(newNickname)) {
            throw new ValueAlreadyInUseException("이미 사용중인 닉네임입니다.");
        }

        user.updateNickname(newNickname);
    }

    private void updateEmail(User user, String newEmail) {
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("유효하지 않은 이메일입니다.");
        }

        if (user.getEmail().equals(newEmail)) {
            throw new ValueAlreadyInUseException("이미 사용중인 이메일입니다.");
        }

        if (userRepository.existsByEmail(newEmail)) {
            throw new ValueAlreadyInUseException("이미 사용중인 이메일입니다.");
        }

        user.updateEmail(newEmail);
    }
}
