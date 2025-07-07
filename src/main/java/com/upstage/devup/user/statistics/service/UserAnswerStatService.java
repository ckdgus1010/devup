package com.upstage.devup.user.statistics.service;

import com.upstage.devup.global.entity.Question;
import com.upstage.devup.global.entity.User;
import com.upstage.devup.global.entity.UserAnswerStat;
import com.upstage.devup.global.exception.UnauthenticatedException;
import com.upstage.devup.user.answer.repository.UserAnswerStatRepository;
import com.upstage.devup.user.statistics.dto.CategoryCountDto;
import com.upstage.devup.user.statistics.dto.UserAnswerStatDto;
import com.upstage.devup.user.statistics.dto.UserCategoryStatDto;
import com.upstage.devup.user.statistics.dto.UserCategoryStatDto.CategoryStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAnswerStatService {

    private final UserAnswerStatRepository userAnswerStatRepository;

    /**
     * 사용자의 문제 풀이 통계 조회
     *
     * @param userId 사용자 ID
     * @return 사용자의 문제 통계
     * @throws UnauthenticatedException 사용자 ID가 null 일 때 발생
     */
    public UserAnswerStatDto getUserAnswerStat(Long userId) {
        if (userId == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        long totalCount = userAnswerStatRepository.countUserAnswerStatByUserId(userId);
        long correctCount = userAnswerStatRepository.countUserAnswerStatByUserIdAndCorrectCountGreaterThan(userId, 0);

        return UserAnswerStatDto.builder()
                .userId(userId)
                .totalCount(totalCount)
                .correctCount(correctCount)
                .build();
    }

    /**
     * 사용자가 푼 문제 카테고리 비율 구하기
     *
     * @param userId 사용자 ID
     * @return 카테고리 비율
     * @throws UnauthenticatedException 사용자 ID가 null 일 때 발생
     */
    public UserCategoryStatDto getUserCategoryStat(Long userId) {
        if (userId == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        List<CategoryCountDto> results = userAnswerStatRepository.findCategoriesByUserId(userId);
        long totalSolvedCount = results.size();

        List<CategoryStat> categoryStats = results.stream()
                .map(result -> CategoryStat.builder()
                        .categoryId(result.getCategoryId())
                        .category(result.getCategory())
                        .color(result.getColor())
                        .solvedCount(result.getCount())
                        .ratio(Math.round((double) result.getCount() / totalSolvedCount * 100.0) * 1.0)
                        .build()
                ).toList();

        return UserCategoryStatDto.builder()
                .userId(userId)
                .totalSolvedCount(totalSolvedCount)
                .categoryStats(categoryStats)
                .build();
    }

    /**
     * 풀이 이력 조회 및 갱신
     * @param isCorrect 정답 여부
     * @param user 사용자 엔티티
     * @param question 질문 엔티티
     * @param now 현재 시간
     */
    @Transactional
    public UserAnswerStat updateUserAnswerStat(boolean isCorrect, User user, Question question, LocalDateTime now) {
        // 엔티티 조회
        UserAnswerStat userAnswerStat = userAnswerStatRepository
                .findByUserIdAndQuestionId(user.getId(), question.getId())
                .orElseGet(
                        () -> UserAnswerStat.builder()
                                .user(user)
                                .question(question)
                                .correctCount(0)
                                .wrongCount(0)
                                .firstSolvedAt(now)
                                .lastSolvedAt(now)
                                .build()
                );

        // 이력 업데이트
        if (isCorrect) {
            userAnswerStat.increaseCorrectCount();
        } else {
            userAnswerStat.increaseWrongCount();
        }

        userAnswerStat.setLastSolvedAt(now);

        return userAnswerStatRepository.save(userAnswerStat);
    }
}
