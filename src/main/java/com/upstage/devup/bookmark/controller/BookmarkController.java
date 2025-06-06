package com.upstage.devup.bookmark.controller;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.bookmark.dto.BookmarkResponseDto;
import com.upstage.devup.bookmark.dto.BookmarksQueryDto;
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

    /**
     * 북마크 목록 조회
     *
     * @param user       로그인한 사용자 정보
     * @param pageNumber 페이지 번호
     * @return 조회된 북마크 목록
     */
    @GetMapping
    public ResponseEntity<BookmarksQueryDto> getBookmarks(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam Integer pageNumber
    ) {
        BookmarksQueryDto result = bookmarkService.getBookmarks(user.getUserId(), pageNumber);
        return ResponseEntity.ok(result);
    }

    /**
     * 신규 북마크 등록
     *
     * @param user       로그인한 사용자 정보
     * @param questionId 등록할 면접 질문 ID
     * @return 등록된 북마크 정보
     */
    @PostMapping("/{questionId}")
    public ResponseEntity<BookmarkResponseDto> createBookmark(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long questionId
    ) {
        BookmarkResponseDto result = bookmarkService.registerBookmark(user.getUserId(), questionId);
        return ResponseEntity.ok(result);
    }

    /**
     * 북마크 삭제
     *
     * @param user       로그인한 사용자 정보
     * @param questionId 삭제할 면접 질문 ID
     * @return 삭제된 북마크 정보
     */
    @DeleteMapping("/{questionId}")
    public ResponseEntity<BookmarkResponseDto> deleteBookmark(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long questionId
    ) {
        BookmarkResponseDto result = bookmarkService.deleteBookmark(user.getUserId(), questionId);
        return ResponseEntity.ok(result);
    }
}
