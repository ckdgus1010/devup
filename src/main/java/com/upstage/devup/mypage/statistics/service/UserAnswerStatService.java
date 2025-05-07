package com.upstage.devup.mypage.statistics.service;

import com.upstage.devup.auth.exception.UnAuthenticatedException;
import com.upstage.devup.mypage.statistics.domain.dto.UserAnswerStatDto;
import com.upstage.devup.mypage.statistics.repository.UserAnswerStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAnswerStatService {

    private final UserAnswerStatRepository userAnswerStatRepository;

    // 사용자 문제 풀이 이력 조회
    public UserAnswerStatDto getUserAnswerStat(Long userId) {
        if (userId == null) {
            throw new UnAuthenticatedException("로그인이 필요합니다.");
        }
        long totalCount = userAnswerStatRepository.countUserAnswerStatByUserId(userId);
        long correctCount = userAnswerStatRepository.countUserAnswerStatByUserIdAndCorrectCountGreaterThan(userId, 0);

        return UserAnswerStatDto.builder()
                .userId(userId)
                .totalCount(totalCount)
                .correctCount(correctCount)
                .build();
    }
}
