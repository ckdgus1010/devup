package com.upstage.devup.bookmark.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class BookmarksQueryDto {

    private final List<BookmarkDetails> contents;

    private final int currentPageNumber;
    private final int size;
    private final int totalPages;
    private final long totalElements;

    private final boolean hasPrevious;
    private final boolean hasNext;

    public BookmarksQueryDto(Page<BookmarkDetails> page) {
        this.contents = page.getContent();

        this.currentPageNumber = page.getNumber();
        this.size = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();

        this.hasPrevious = page.hasPrevious();
        this.hasNext = page.hasNext();
    }
}
