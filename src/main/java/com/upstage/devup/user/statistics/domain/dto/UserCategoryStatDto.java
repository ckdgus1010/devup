package com.upstage.devup.user.statistics.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCategoryStatDto {

    private Long userId;
    private Long totalSolvedCount;
    private List<CategoryStat> categoryStats;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CategoryStat {
        private String category;
        private Long solvedCount;
        private Double ratio;
    }
}
