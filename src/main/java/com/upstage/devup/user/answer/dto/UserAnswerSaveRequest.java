package com.upstage.devup.user.answer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerSaveRequest {

    @NotNull(message = "유효한 면접 질문이 아닙니다.")
    private Long questionId;

    @NotBlank(message = "정답을 입력해 주세요.")
    private String answerText;

    @NotNull(message = "정답 여부를 확인해 주세요.")
    private Boolean isCorrect;

}
