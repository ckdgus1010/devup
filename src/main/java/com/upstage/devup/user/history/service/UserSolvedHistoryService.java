package com.upstage.devup.user.history.service;

import com.upstage.devup.global.entity.UserAnswer;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.global.exception.UnauthenticatedException;
import com.upstage.devup.user.history.dto.UserSolvedHistoryDetailDto;
import com.upstage.devup.user.history.repository.UserSolvedHistoryRepository;
import com.upstage.devup.user.history.dto.UserSolvedQuestionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSolvedHistoryService {

    private final UserSolvedHistoryRepository userSolvedHistoryRepository;

    private static final int USER_SOLVED_QUESTIONS_PER_PAGE = 10;


    // 문제 풀이 이력 조회
    public Page<UserSolvedQuestionDto> getUserSolvedQuestions(Long userId, Integer pageNumber) {
        if (userId == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageNumber, USER_SOLVED_QUESTIONS_PER_PAGE, sort);

        return userSolvedHistoryRepository
                .findByUserId(userId, pageable)
                .map(UserSolvedQuestionDto::of);
    }

    /**
     * 상세 문제 풀이 이력 조회
     *
     * @param userAnswerId 사용자 답안 ID
     * @return 상세 문제 풀이 이력
     */
    public UserSolvedHistoryDetailDto getUserSolvedHistoryDetail(Long userAnswerId) {
        if (userAnswerId == null || userAnswerId <= 0L) {
            throw new IllegalArgumentException("풀이 이력을 찾을 수 없습니다.");
        }

        UserAnswer userAnswer = userSolvedHistoryRepository.findById(userAnswerId)
                .orElseThrow(() -> new EntityNotFoundException("풀이 이력을 찾을 수 없습니다."));

        return UserSolvedHistoryDetailDto.of(userAnswer);
    }
}
