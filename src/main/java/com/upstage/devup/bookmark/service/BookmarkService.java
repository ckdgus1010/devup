package com.upstage.devup.bookmark.service;

import com.upstage.devup.bookmark.dto.BookmarkDetails;
import com.upstage.devup.bookmark.dto.BookmarkResponseDto;
import com.upstage.devup.bookmark.dto.BookmarksQueryDto;
import com.upstage.devup.bookmark.repository.BookmarkRepository;
import com.upstage.devup.global.domain.id.BookmarkId;
import com.upstage.devup.global.entity.Bookmark;
import com.upstage.devup.global.entity.Question;
import com.upstage.devup.global.entity.User;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.question.repository.QuestionRepository;
import com.upstage.devup.user.account.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserAccountRepository userAccountRepository;
    private final QuestionRepository questionRepository;

    private static final int BOOKMARKS_PER_PAGE = 10;

    /**
     * 북마크 목록 조회
     *
     * @param userId     사용자 ID
     * @param pageNumber 페이지 번호
     * @return 조회된 북마크 목록
     */
    public BookmarksQueryDto getBookmarks(long userId, int pageNumber) {
        Page<BookmarkDetails> result = bookmarkRepository.findBookmarkDetailsByUserId(
                userId,
                PageRequest.of(
                        Math.max(0, pageNumber),
                        BOOKMARKS_PER_PAGE,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
        );

        return new BookmarksQueryDto(result);
    }

    /**
     * 신규 북마크 등록
     * (이미 등록된 북마크가 있는 경우 정상 등록 처리)
     *
     * @param userId     사용자 ID
     * @param questionId 면접 질문 ID
     * @return 저장된 북마크 정보
     */
    public BookmarkResponseDto registerBookmark(long userId, long questionId) {

        Bookmark bookmark = bookmarkRepository
                .findByUserIdAndQuestionId(userId, questionId)
                .orElseGet(() -> {
                    User user = userAccountRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

                    Question question = questionRepository.findById(questionId)
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 질문입니다."));

                    return bookmarkRepository.save(
                            Bookmark.builder()
                                    .id(new BookmarkId(userId, questionId))
                                    .user(user)
                                    .question(question)
                                    .createdAt(LocalDateTime.now())
                                    .build()
                    );
                });

        return convertToDto(bookmark);
    }

    /**
     * 북마크 삭제
     *
     * @param userId     사용자 ID
     * @param questionId 면접 질문 ID
     * @return 삭제된 북마크 정보
     */
    public BookmarkResponseDto deleteBookmark(long userId, long questionId) {

        Bookmark entity = bookmarkRepository
                .findByUserIdAndQuestionId(userId, questionId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 북마크입니다."));

        bookmarkRepository.delete(entity);

        return convertToDto(entity);
    }

    private BookmarkResponseDto convertToDto(Bookmark bookmark) {
        return BookmarkResponseDto.builder()
                .userId(bookmark.getUser().getId())
                .questionId(bookmark.getQuestion().getId())
                .createdAt(bookmark.getCreatedAt())
                .build();
    }

    public boolean checkBookmarkIsRegistered(long userId, long questionId) {
        return bookmarkRepository.existsByUserIdAndQuestionId(userId, questionId);
    }
}
