package com.upstage.devup.user.answer.domain.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerSaveRequest {

    private Long questionId;
    private String answerText;
    private Boolean isCorrect;

}
