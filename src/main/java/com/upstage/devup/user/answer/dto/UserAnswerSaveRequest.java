package com.upstage.devup.user.answer.dto;

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
