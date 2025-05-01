package com.upstage.devup.answer.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDetailDto {

    private Long id;
    private String answerText;

    private Long questionId;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
