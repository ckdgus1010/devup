package com.upstage.devup.admin.level.dto;

import jakarta.validation.constraints.NotBlank;

public record LevelAddRequest(
        @NotBlank(message = "난이도를 입력해 주세요.")
        String levelName
) {
}
