package com.upstage.devup.user.wrong.service;

import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.global.entity.Question;
import com.upstage.devup.global.entity.UserWrongAnswer;
import com.upstage.devup.user.answer.repository.UserWrongAnswerRepository;
import com.upstage.devup.user.statistics.dto.WrongNoteSummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserWrongAnswerQueryService {

    private final UserWrongAnswerRepository userWrongAnswerRepository;

    private static final int WRONG_NOTES_PER_PAGE = 10;

    // 유저 오답 문제 목록 조회

    /**
     * 사용자의 오답 노트 목록 조회
     *
     * @param userId     사용자 ID
     * @param pageNumber 페이지 번호
     * @return 조회된 문제 목록
     * @throws EntityNotFoundException  사용자 ID가 null일 때 발생
     * @throws IllegalArgumentException pageNumber가 null이거나 음수일 때 발생
     */
    public Page<WrongNoteSummaryDto> getWrongNoteSummaries(long userId, int pageNumber) {

        Pageable pageable = PageRequest.of(
                pageNumber,
                WRONG_NOTES_PER_PAGE,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return userWrongAnswerRepository
                .findByUserId(userId, pageable)
                .map(this::toWrongNoteSummaryDto);
    }

    private WrongNoteSummaryDto toWrongNoteSummaryDto(UserWrongAnswer entity) {
        Question question = entity.getQuestion();

        return WrongNoteSummaryDto.builder()
                .userId(entity.getUser().getId())
                .questionId(question.getId())
                .title(question.getTitle())
                .category(question.getCategory().getCategory())
                .level(question.getLevel().getLevel())
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
