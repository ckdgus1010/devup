package com.upstage.devup.user.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerStatDto {

    private Long userId;
    private Long totalCount;
    private Long correctCount;

}
