package com.upstage.devup.bookmark.service;

import com.upstage.devup.bookmark.dto.BookmarkResponseDto;
import com.upstage.devup.bookmark.repository.BookmarkRepository;
import com.upstage.devup.global.domain.id.BookmarkId;
import com.upstage.devup.global.entity.*;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.question.repository.QuestionRepository;
import com.upstage.devup.user.account.repository.UserAccountRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class BookmarkServiceTest {

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private QuestionRepository questionRepository;

    private Long userId;
    private Long questinId;

    @BeforeEach
    public void beforeEach() {
        User user = userAccountRepository.save(User.builder()
                .loginId("user1234")
                .password("pass1234")
                .nickname("nickname1234")
                .email("user1234@gmail.com")
                .createdAt(LocalDateTime.now())
                .build());

        Question question = questionRepository.save(Question.builder()
                .title("제목 1")
                .questionText("질문 내용")
                .category(Category.builder().id(1L).build())
                .level(Level.builder().id(2L).build())
                .createdAt(LocalDateTime.now())
                .build());

        this.userId = user.getId();
        this.questinId = question.getId();
    }

    @Nested
    @DisplayName("북마크 등록 기능 테스트")
    public class BookmarkRegistration {

        @Nested
        @DisplayName("북마크 등록 성공")
        public class SuccessCases {

            @Test
            @DisplayName("userId와 questionId가 유효한 값이면 성공")
            public void shouldReturnBookmarkResponseDto_whenUserIdAndQuestionIdAreValid() {
                // given

                // when
                BookmarkResponseDto result = bookmarkService.registerBookmark(userId, questinId);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getUserId()).isEqualTo(userId);
                assertThat(result.getQuestionId()).isEqualTo(questinId);
                assertThat(result.getCreatedAt()).isNotNull();
                assertThat(result.getCreatedAt()).isBefore(LocalDateTime.now());
            }

            @Test
            @DisplayName("이미 저장된 북마크가 있는 경우 정상 등록 처리")
            public void shouldReturnBookmarkResponseDto_whenUserIdAndQuestionIdAreAlreadyUsed() {
                // given
                Bookmark entity = bookmarkRepository.save(
                        Bookmark.builder()
                                .id(new BookmarkId(userId, questinId))
                                .user(User.builder().id(userId).build())
                                .question(Question.builder().id(questinId).build())
                                .createdAt(LocalDateTime.now())
                                .build()
                );

                // when
                BookmarkResponseDto result = bookmarkService.registerBookmark(userId, questinId);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getUserId()).isEqualTo(userId);
                assertThat(result.getQuestionId()).isEqualTo(questinId);
                assertThat(result.getCreatedAt()).isEqualTo(entity.getCreatedAt());
            }
        }

        @Nested
        @DisplayName("북마크 등록 실패")
        public class FailureCases {

            @Test
            @DisplayName("유효하지 않은 userId(0)를 사용한 경우")
            public void shouldThrowEntityNotFoundException_whenUserIdIsZero() {
                // given
                long wrongUserId = 0L;

                // when
                EntityNotFoundException exception = assertThrows(
                        EntityNotFoundException.class,
                        () -> bookmarkService.registerBookmark(wrongUserId, questinId)
                );

                assertThat(exception.getMessage()).isEqualTo("존재하지 않는 회원입니다.");
            }

            @Test
            @DisplayName("유효하지 않은 userId(음수)를 사용한 경우")
            public void shouldThrowEntityNotFoundException_whenUserIdIsNegative() {
                // given
                long wrongUserId = -1L;

                // when
                EntityNotFoundException exception = assertThrows(
                        EntityNotFoundException.class,
                        () -> bookmarkService.registerBookmark(wrongUserId, questinId)
                );

                assertThat(exception.getMessage()).isEqualTo("존재하지 않는 회원입니다.");
            }

            @Test
            @DisplayName("유효하지 않은 questionId(0)를 사용한 경우")
            public void shouldThrowEntityNotFoundException_whenQuestionIdIsZero() {
                // given
                long wrongQuestionId = 0L;

                // when
                EntityNotFoundException exception = assertThrows(
                        EntityNotFoundException.class,
                        () -> bookmarkService.registerBookmark(userId, wrongQuestionId)
                );

                assertThat(exception.getMessage()).isEqualTo("존재하지 않는 질문입니다.");
            }

            @Test
            @DisplayName("유효하지 않은 questionId(음수)를 사용한 경우")
            public void shouldThrowEntityNotFoundException_whenQuestionIdIsNegative() {
                // given
                long wrongQuestionId = -1L;

                // when
                EntityNotFoundException exception = assertThrows(
                        EntityNotFoundException.class,
                        () -> bookmarkService.registerBookmark(userId, wrongQuestionId)
                );

                assertThat(exception.getMessage()).isEqualTo("존재하지 않는 질문입니다.");
            }

            @Test
            @DisplayName("유효하지 않은 userId와 questionId를 사용한 경우")
            public void shouldThrowEntityNotFoundException_whenUserIdAndQuestionIdAreUnavailable() {
                // given
                long wrongUserId = -1L;
                long wrongQuestionId = 0L;

                // when
                EntityNotFoundException exception = assertThrows(
                        EntityNotFoundException.class,
                        () -> bookmarkService.registerBookmark(wrongUserId, wrongQuestionId)
                );

                assertThat(exception.getMessage()).isEqualTo("존재하지 않는 회원입니다.");
            }
        }
    }
}