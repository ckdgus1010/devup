package com.upstage.devup.ai.dto;

public record AiAnswerCheckResponseDto(
        boolean isCorrect,
        String feedback
) {
}
