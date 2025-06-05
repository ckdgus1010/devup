package com.upstage.devup.bookmark.controller;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.bookmark.dto.BookmarkResponseDto;
import com.upstage.devup.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/{questionId}")
    public ResponseEntity<BookmarkResponseDto> deleteBookmark(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long questionId
    ) {
        BookmarkResponseDto result = bookmarkService.deleteBookmark(user.getUserId(), questionId);
        return ResponseEntity.ok(result);
    }
}
