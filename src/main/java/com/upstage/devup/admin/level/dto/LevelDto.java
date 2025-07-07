package com.upstage.devup.admin.level.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record LevelDto(
        long levelId,
        String levelName,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime modifiedAt
) {
}
