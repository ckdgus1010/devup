package com.upstage.devup.user.account.service;

import com.upstage.devup.auth.domain.entity.User;
import com.upstage.devup.auth.respository.UserRepository;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.user.account.dto.UserAccountDto;
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
}
