package com.upstage.devup.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryUpdateRequest(

        @NotNull(message = "변경할 카테고리를 선택해 주세요.")
        Long categoryId,

        @NotBlank(message = "카테고리를 입력해 주세요.")
        String category,

        @NotBlank(message = "색상을 입력해 주세요.")
        String color
) {
}
