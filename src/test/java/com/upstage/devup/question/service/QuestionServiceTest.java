package com.upstage.devup.question.service;

import com.upstage.devup.bookmark.repository.BookmarkRepository;
import com.upstage.devup.global.domain.id.BookmarkId;
import com.upstage.devup.global.entity.*;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.question.dto.QuestionDetailDto;
import com.upstage.devup.question.repository.QuestionRepository;
import com.upstage.devup.user.account.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class QuestionServiceTest {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Nested
    @DisplayName("면접 질문 페이지 조회")
    public class QuestionPagingQuery {

        @Test
        @DisplayName("면접 질문 페이지 조회 - 성공")
        public void successGetQuestions() {
            // given
            int page = 0;

            // when
            Page<QuestionDetailDto> questions = questionService.getQuestions(page);

            // then
            assertThat(questions.getContent().size()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("면접 질문 제목 검색")
    public class QuestionSearch {

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("제목 검색 - 성공")
            public void successToSearchQuestionByTitle() {
                // given
                String title = "Spring";
                int page = 0;

                // when
                Page<QuestionDetailDto> results = questionService.searchQuestionsByTitle(title, page);

                // then
                assertThat(results.getTotalElements()).isGreaterThan(0);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @Test
            @DisplayName("검색어가 공백인 경우 - 빈 값을 반환")
            public void failToSearchQuestionByTitleUsingSpace() {
                // given
                String title = "     ";
                int page = 0;

                // when
                Page<QuestionDetailDto> results = questionService.searchQuestionsByTitle(title, page);

                // then
                assertThat(results.getTotalElements()).isEqualTo(0);
            }

            @Test
            @DisplayName("검색어의 길이가 2보다 작을 경우 - 빈 값을 반환")
            public void failToSearchQuestionByTitleUsingShortTitle() {
                // given
                String title = "1";
                int page = 0;

                // when
                Page<QuestionDetailDto> results = questionService.searchQuestionsByTitle(title, page);

                // then
                assertThat(results.getTotalElements()).isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("면접 질문 단건 조회")
    public class QuestionSingleQuery {

        User user;
        Question question;

        @BeforeEach
        public void beforeEach() {
            user = userAccountRepository.save(User.builder()
                    .loginId("test")
                    .password("1234")
                    .nickname("test_user")
                    .email("test@gmail.com")
                    .createdAt(LocalDateTime.now())
                    .build());
            question = questionRepository.save(Question.builder()
                    .title("제목123")
                    .questionText("내용123")
                    .category(Category.builder().id(1L).build())
                    .level(Level.builder().id(2L).build())
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("로그인하지 않은 경우")
            public void successFindQuestionById() {
                // given
                long userId = 0L;

                // when
                QuestionDetailDto result = questionService.getQuestion(userId, question.getId());

                // then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(question.getId());
                assertThat(result.getTitle()).isEqualTo(question.getTitle());
                assertThat(result.getQuestionText()).isEqualTo(question.getQuestionText());
                assertThat(result.getCategory()).isEqualTo(question.getCategory().getCategory());
                assertThat(result.getLevel()).isEqualTo(question.getLevel().getLevel());
                assertThat(result.isBookmarked()).isFalse();
                assertThat(result.getCreatedAt()).isEqualTo(question.getCreatedAt());
                assertThat(result.getModifiedAt()).isNull();
            }

            @Test
            @DisplayName("로그인한 사용자, 북마크 등록되지 않은 면접 질문인 경우")
            public void shouldReturnQuestionDetailDto_whenUserIsSignIn() {
                // given

                // when
                QuestionDetailDto result = questionService.getQuestion(user.getId(), question.getId());

                // then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(question.getId());
                assertThat(result.getTitle()).isEqualTo(question.getTitle());
                assertThat(result.getQuestionText()).isEqualTo(question.getQuestionText());
                assertThat(result.getCategory()).isEqualTo(question.getCategory().getCategory());
                assertThat(result.getLevel()).isEqualTo(question.getLevel().getLevel());
                assertThat(result.isBookmarked()).isFalse();
                assertThat(result.getCreatedAt()).isEqualTo(question.getCreatedAt());
                assertThat(result.getModifiedAt()).isNull();
            }

            @Test
            @DisplayName("로그인한 사용자, 북마크 등록된 면접 질문인 경우")
            public void shouldReturnQuestionDetailDto_whenUserIsSignInAndBookmarkIsRegistered() {
                // given
                bookmarkRepository.save(Bookmark.builder()
                        .id(new BookmarkId(user.getId(), question.getId()))
                        .user(user)
                        .question(question)
                        .createdAt(LocalDateTime.now())
                        .build());

                // when
                QuestionDetailDto result = questionService.getQuestion(user.getId(), question.getId());

                // then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(question.getId());
                assertThat(result.getTitle()).isEqualTo(question.getTitle());
                assertThat(result.getQuestionText()).isEqualTo(question.getQuestionText());
                assertThat(result.getCategory()).isEqualTo(question.getCategory().getCategory());
                assertThat(result.getLevel()).isEqualTo(question.getLevel().getLevel());
                assertThat(result.isBookmarked()).isTrue();
                assertThat(result.getCreatedAt()).isEqualTo(question.getCreatedAt());
                assertThat(result.getModifiedAt()).isNull();
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @Test
            @DisplayName("로그인하지 않은 사용자, 유효하지 않은 ID로 조회하는 경우 - EntityNotFoundException 반환")
            public void shouldThrowEntityNotFoundException_whenUserIsNotSignInAndQuestionIdIsUnavailable() {
                // given
                long userId = 0L;
                long questionId = 0L;

                // when & then
                EntityNotFoundException exception = assertThrows(
                        EntityNotFoundException.class,
                        () -> questionService.getQuestion(userId, questionId)
                );

                assertThat(exception.getMessage()).isEqualTo("면접 질문을 찾을 수 없습니다.");
            }

            @Test
            @DisplayName("로그인한 사용자, 유효하지 않은 ID로 조회하는 경우 - EntityNotFoundException 반환")
            public void failToGetQuestion_whenUsingInvalidId() {
                // given
                long userId = user.getId();
                long questionId = 0L;

                // when & then
                EntityNotFoundException exception = assertThrows(
                        EntityNotFoundException.class,
                        () -> questionService.getQuestion(userId, questionId)
                );

                assertThat(exception.getMessage()).isEqualTo("면접 질문을 찾을 수 없습니다.");
            }
        }
    }
}