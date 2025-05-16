package com.upstage.devup.question.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QuestionDetailDto {

    private Long id;
    private String title;
    private String questionText;

    private String category;
    private String level;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
