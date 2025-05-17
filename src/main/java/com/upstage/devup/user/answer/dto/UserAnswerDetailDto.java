package com.upstage.devup.user.answer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
