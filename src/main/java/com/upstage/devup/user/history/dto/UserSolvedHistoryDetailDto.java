package com.upstage.devup.user.history.dto;

import com.upstage.devup.global.entity.Question;
import com.upstage.devup.global.entity.UserAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserSolvedHistoryDetailDto {

    private Long userId;

    private Long questionId;
    private String questionTitle;
    private String questionText;
    private String category;
    private String level;

    private String userAnswerText;

    private LocalDateTime solvedAt;
    private boolean isCorrect;

    public static UserSolvedHistoryDetailDto of(UserAnswer entity) {
        if (entity == null) {
            return null;
        }

        Question question = entity.getQuestion();

        return UserSolvedHistoryDetailDto.builder()
                .userId(entity.getUser().getId())
                .questionId(question.getId())
                .questionTitle(question.getTitle())
                .questionText(question.getQuestionText())
                .category(question.getCategory().getCategoryName())
                .level(question.getLevel().getLevelName())
                .userAnswerText(entity.getAnswerText())
                .solvedAt(entity.getCreatedAt())
                .isCorrect(entity.getIsCorrect())
                .build();
    }
}
