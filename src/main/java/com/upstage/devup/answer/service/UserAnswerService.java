package com.upstage.devup.answer.service;

import com.upstage.devup.answer.domain.dto.UserAnswerSaveRequest;
import com.upstage.devup.answer.domain.entity.UserAnswer;
import com.upstage.devup.answer.repository.UserAnswerRepository;
import com.upstage.devup.auth.domain.entity.User;
import com.upstage.devup.auth.service.UserService;
import com.upstage.devup.question.domain.entity.Question;
import com.upstage.devup.question.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAnswerService {

    private final BoardService boardService;
    private final UserService userService;

    private final UserAnswerRepository userAnswerRepository;

    /**
     * 사용자가 작성한 정답 저장
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

}
