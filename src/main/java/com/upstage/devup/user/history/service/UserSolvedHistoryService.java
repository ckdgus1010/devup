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
    public Page<UserSolvedQuestionDto> getUserSolvedQuestions(long userId, int pageNumber) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt", "id");
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber), USER_SOLVED_QUESTIONS_PER_PAGE, sort);

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
    public UserSolvedHistoryDetailDto getUserSolvedHistoryDetail(long userAnswerId) {

        UserAnswer userAnswer = userSolvedHistoryRepository.findById(userAnswerId)
                .orElseThrow(() -> new EntityNotFoundException("풀이 이력을 찾을 수 없습니다."));

        return UserSolvedHistoryDetailDto.of(userAnswer);
    }
}
