package com.upstage.devup.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AiAnswerCheckRequestDto(
        @NotNull
        Long questionId,

        @NotBlank
        String userAnswer
) {
}
