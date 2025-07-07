package com.upstage.devup.global.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public abstract class PageDto<T> {

    private final int number;
    private final int size;
    private final int totalPages;
    private final long totalElements;

    private final boolean hasPrevious;
    private final boolean hasNext;

    private final List<T> content;

    public PageDto(@NotNull Page<T> page) {
        this.number = page.getNumber();
        this.size = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();

        this.hasPrevious = page.hasPrevious();
        this.hasNext = page.hasNext();

        this.content = page.getContent();
    }
}
