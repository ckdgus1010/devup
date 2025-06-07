package com.upstage.devup.user.wrong.service;

import com.upstage.devup.auth.respository.UserAuthRepository;
import com.upstage.devup.global.entity.*;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.question.repository.QuestionRepository;
import com.upstage.devup.user.account.repository.UserAccountRepository;
import com.upstage.devup.user.wrong.repository.UserWrongNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Nested
@Transactional
@SpringBootTest
class UserWrongNoteDeleteServiceTest {

    @Autowired
    private UserWrongNoteDeleteService userWrongNoteDeleteService;

    @Autowired
    private UserWrongNoteRepository userWrongNoteRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private QuestionRepository questionRepository;

    private long userId;
    private long questionId;

    @BeforeEach
    public void beforeEach() {
        User user = userAccountRepository.save(User.builder()
                .role(Role.builder().id(1L).build())
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

        UserWrongAnswer userWrongAnswer = UserWrongAnswer.builder()
                .user(user)
                .question(question)
                .createdAt(LocalDateTime.now())
                .build();

        userWrongNoteRepository.save(userWrongAnswer);

        this.userId = user.getId();
        this.questionId = question.getId();
    }

    @Nested
    @DisplayName("성공 케이스")
    public class SuccessCases {

        @Test
        @DisplayName("유효한 사용자 ID, 질문 ID를 사용한 경우")
        public void shouldDeleteWrongNote_whenExistsInDB() {
            // given

            // when
            userWrongNoteDeleteService.deleteWrongNote(userId, questionId);

            // then
            assertFalse(userWrongNoteRepository.existsByUserIdAndQuestionId(userId, questionId));
        }

    }

    @Nested
    @DisplayName("실패 케이스")
    public class FailureCase {

        @ParameterizedTest
        @CsvSource({
                "0, 0",
                "-1, 2",
                "1, -2"
        })
        @DisplayName("DB에 없는 오답노트를 삭제하려는 경우 EntityNotFoundException 예외 발생")
        public void shouldThrowEntityNotFoundException_whenWrongNoteDoesNotExistInDB(long userId, long questionId) {
            // given

            // when & then  
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> userWrongNoteDeleteService.deleteWrongNote(userId, questionId)
            );

            assertThat(exception.getMessage()).isEqualTo("오답노트를 찾을 수 없습니다.");
        }
    }
}