package com.upstage.devup.user.answer.service;

import com.upstage.devup.answer.domain.dto.UserAnswerDetailDto;
import com.upstage.devup.user.answer.domain.entity.UserAnswer;
import com.upstage.devup.user.answer.domain.entity.UserWrongAnswer;
import com.upstage.devup.user.answer.repository.UserAnswerRepository;
import com.upstage.devup.user.answer.repository.UserWrongAnswerRepository;
import com.upstage.devup.auth.domain.entity.User;
import com.upstage.devup.auth.service.UserService;
import com.upstage.devup.question.domain.entity.Question;
import com.upstage.devup.question.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAnswerService {

    private final BoardService boardService;
    private final UserService userService;

    private final UserAnswerRepository userAnswerRepository;
    private final UserWrongAnswerRepository userWrongAnswerRepository;

    private static final int USER_ANSWERS_PER_PAGE = 10;

    /**
     * 사용자가 작성한 답안 목록 조회
     * <p>
     * - 동일한 질문에 대한 답안이라면 가장 최근 답안으로 조회
     *
     * @param userId 조회할 사용자 ID
     * @param pageNumber 페이지 번호
     * @return 조회된 결과
     */
    public Page<UserAnswerDetailDto> getUserAnswers(Long userId, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, USER_ANSWERS_PER_PAGE);
        return userAnswerRepository
                .findLatestAnswersByUserId(userId, pageable)
                .map(this::convertUserAnswerToUserAnswerDetailDto);
    }

    /**
     * 사용자가 작성한 답안 단건 조회
     * <p>
     * - 동일한 질문에 대한 답안이라면 가장 최근 답안으로 조회
     *
     * @param userId 조회할 사용자 ID
     * @param questionId 조회할 면접 질문 ID
     * @return 조회된 결과
     */
    public UserAnswerDetailDto getUserAnswer(Long userId, Long questionId) {
        Optional<UserAnswer> optional
                = userAnswerRepository.findTopByUserIdAndQuestionIdOrderByCreatedAtDesc(userId, questionId);

        if (optional.isEmpty()) {
            return null;
        }

        return convertUserAnswerToUserAnswerDetailDto(optional.get());
    }

    private UserAnswerDetailDto convertUserAnswerToUserAnswerDetailDto(UserAnswer entity) {
        return UserAnswerDetailDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .questionId(entity.getQuestion().getId())
                .answerText(entity.getAnswerText())
                .isCorrect(entity.getIsCorrect())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * 사용자가 틀린 문제 단건 조회
     *
     * @param userId 조회할 사용자 ID
     * @param questionId 조회할 면접 문제 ID
     * @return 조회된 결과
     */
    public UserAnswerDetailDto getUserWrongAnswer(Long userId, Long questionId) {
        Optional<UserWrongAnswer> optional
                = userWrongAnswerRepository.findTopByUserIdAndQuestionIdOrderByCreatedAtDesc(userId, questionId);

        if (optional.isEmpty()) {
            return null;
        }

        UserWrongAnswer entity = optional.get();

        return UserAnswerDetailDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .questionId(entity.getQuestion().getId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
