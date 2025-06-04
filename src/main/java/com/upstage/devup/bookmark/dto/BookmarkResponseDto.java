package com.upstage.devup.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class BookmarkResponseDto {

    private Long userId;
    private Long questionId;
    private LocalDateTime createdAt;

}
