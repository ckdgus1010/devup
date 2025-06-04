package com.upstage.devup.bookmark.controller;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.bookmark.dto.BookmarkResponseDto;
import com.upstage.devup.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{questionId}")
    public ResponseEntity<BookmarkResponseDto> createBookmark(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long questionId
    ) {
        BookmarkResponseDto result = bookmarkService.registerBookmark(user.getUserId(), questionId);
        return ResponseEntity.ok(result);
    }
}
