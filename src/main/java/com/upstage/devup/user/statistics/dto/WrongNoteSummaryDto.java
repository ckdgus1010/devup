package com.upstage.devup.user.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongNoteSummaryDto {

    private Long userId;

    private Long questionId;
    private String title;
    private String category;
    private String level;

    private LocalDateTime createdAt;
}
