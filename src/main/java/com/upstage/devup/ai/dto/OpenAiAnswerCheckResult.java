package com.upstage.devup.ai.dto;

public record OpenAiAnswerCheckResult(
        boolean isCorrect,
        String feedback
) {
}
