package com.upstage.devup.user.answer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerDetailDto {

    private Long userId;

    private Long questionId;
    private String title;
    private String questionText;
    private String category;
    private String level;

    private Long userAnswerId;
    private String answerText;

    private Boolean isCorrect;
    private LocalDateTime createdAt;
}
