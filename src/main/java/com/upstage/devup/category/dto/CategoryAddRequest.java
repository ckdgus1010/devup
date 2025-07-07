package com.upstage.devup.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryAddRequest(
        @NotBlank(message = "카테고리를 입력해 주세요.")
        String categoryName,

        @NotBlank(message = "색상을 입력해 주세요.")
        String color
) {

}
