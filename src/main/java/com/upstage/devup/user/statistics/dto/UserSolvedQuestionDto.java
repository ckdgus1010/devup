package com.upstage.devup.user.statistics.dto;

import com.upstage.devup.global.entity.UserAnswerStat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserSolvedQuestionDto {

    private Long id;

    private Long questionId;
    private String questionTitle;
    private String category;
    private String level;
    private LocalDateTime firstSolvedAt;
    private LocalDateTime lastSolvedAt;

    private boolean isCorrect;

    public static UserSolvedQuestionDto of(UserAnswerStat entity) {
        if (entity == null) {
            return null;
        }

        return UserSolvedQuestionDto.builder()
                .id(entity.getId())
                .questionId(entity.getQuestion().getId())
                .questionTitle(entity.getQuestion().getTitle())
                .category(entity.getQuestion().getCategory().getCategory())
                .level(entity.getQuestion().getLevel().getLevel())
                .firstSolvedAt(entity.getFirstSolvedAt())
                .lastSolvedAt(entity.getLastSolvedAt())
                .isCorrect(entity.getCorrectCount() > 0)
                .build();
    }
}
