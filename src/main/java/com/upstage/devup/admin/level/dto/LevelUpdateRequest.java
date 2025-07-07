package com.upstage.devup.admin.level.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LevelUpdateRequest(
        @NotNull(message = "난이도를 선택해 주세요.")
        Long levelId,

        @NotBlank(message = "난이도를 입력해 주세요.")
        String levelName
) {
}
