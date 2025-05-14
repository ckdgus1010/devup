package com.upstage.devup.user.statistics.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class CategoryCountDto {

    private Long categoryId;
    private String category;
    private String color;
    private long count;

}
