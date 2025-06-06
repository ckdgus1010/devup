package com.upstage.devup.bookmark.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record BookmarkDetails(
        Long questionId,
        String title,
        String category,
        String level,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) {

    public BookmarkDetails(Long questionId, String title, String category, String level, LocalDateTime createdAt) {
        this.questionId = questionId;
        this.title = title;
        this.category = category;
        this.level = level;
        this.createdAt = createdAt;
    }
}
