package com.upstage.devup.answer.service;

import com.upstage.devup.answer.domain.dto.UserAnswerDetailDto;
import com.upstage.devup.answer.domain.dto.UserAnswerSaveRequest;
import com.upstage.devup.answer.domain.entity.UserAnswer;
import com.upstage.devup.answer.domain.entity.UserCorrectAnswer;
import com.upstage.devup.answer.domain.entity.UserWrongAnswer;
import com.upstage.devup.answer.repository.UserAnswerRepository;
import com.upstage.devup.answer.repository.UserCorrectAnswerRepository;
import com.upstage.devup.answer.repository.UserWrongAnswerRepository;
import com.upstage.devup.auth.domain.entity.User;
import com.upstage.devup.auth.service.UserService;
import com.upstage.devup.question.domain.entity.Question;
import com.upstage.devup.question.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final UserCorrectAnswerRepository userCorrectAnswerRepository;
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
     * 사용자가 맞춘 문제 단건 조회
     *
     * @param userId 조회할 사용자 ID
     * @param questionId 조회할 면접 문제 ID
     * @return 조회된 결과
     */
    public UserAnswerDetailDto getUserCorrectAnswer(Long userId, Long questionId) {
        Optional<UserCorrectAnswer> optional
                = userCorrectAnswerRepository.findTopByUserIdAndQuestionIdOrderByCreatedAtDesc(userId, questionId);

        if (optional.isEmpty()) {
            return null;
        }

        UserCorrectAnswer entity = optional.get();

        return UserAnswerDetailDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .questionId(entity.getQuestion().getId())
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

    /**
     * 사용자가 작성한 정답 저장
     * <p>
     * - 정답 여부에 따라 정답/오답 테이블에도 각각 저장됨
     *
     * @param userId 사용자 ID
     * @param request 사용자 작성 정답 데이터
     * @return 저장된 엔티티 ID
     */
    public Long saveUserAnswer(Long userId, UserAnswerSaveRequest request) {
        if (!userService.isUserIdInUse(userId)) {
            return null;
        }

        Long questionId = request.getQuestionId();

        if (!boardService.isQuestionIdInUse(questionId)) {
            return null;
        }

        // 정답, 오답 구분해서 저장
        if (request.getIsCorrect()) {
            saveUserCorrectAnswer(userId, questionId);
        } else {
            saveUserWrongAnswer(userId, questionId);
        }

        // ID만 설정한 연관관계 참조용 객체
        User userRef = User.builder().id(userId).build();
        Question questionRef = Question.builder().id(questionId).build();

        UserAnswer entity = UserAnswer.builder()
                .user(userRef)
                .question(questionRef)
                .answerText(request.getAnswerText())
                .isCorrect(request.getIsCorrect())
                .createdAt(LocalDateTime.now())
                .build();

        return userAnswerRepository.save(entity).getId();
    }

    /**
     * 사용자의 정답 답안 저장
     *
     * @param userId 사용자 ID
     * @param questionId 면접 질문 ID
     * @return 저장된 엔티티 ID
     */
    private Long saveUserCorrectAnswer(Long userId, Long questionId) {

        // ID만 설정한 연관관계 참조용 객체
        User userRef = User.builder().id(userId).build();
        Question questionRef = Question.builder().id(questionId).build();

        UserCorrectAnswer entity = UserCorrectAnswer.builder()
                .user(userRef)
                .question(questionRef)
                .createdAt(LocalDateTime.now())
                .build();

        return userCorrectAnswerRepository.save(entity).getId();
    }

    /**
     * 사용자의 오답 답안 저장
     *
     * @param userId 사용자 ID
     * @param questionId 면접 질문 ID
     * @return 저장된 엔티티 ID
     */
    private Long saveUserWrongAnswer(Long userId, Long questionId) {

        // ID만 설정한 연관관계 참조용 객체
        User userRef = User.builder().id(userId).build();
        Question questionRef = Question.builder().id(questionId).build();

        UserWrongAnswer entity = UserWrongAnswer.builder()
                .user(userRef)
                .question(questionRef)
                .createdAt(LocalDateTime.now())
                .build();

        return userWrongAnswerRepository.save(entity).getId();
    }

}
