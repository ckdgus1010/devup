package com.upstage.devup.admin.level.service;

import com.upstage.devup.admin.level.dto.LevelAddRequest;
import com.upstage.devup.admin.level.dto.LevelDto;
import com.upstage.devup.admin.level.dto.LevelUpdateRequest;
import com.upstage.devup.admin.level.repository.LevelRepository;
import com.upstage.devup.global.entity.Level;
import com.upstage.devup.global.exception.DuplicatedResourceException;
import com.upstage.devup.global.exception.EntityNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class LevelServiceTest {

    @Autowired
    private LevelService levelService;

    @Autowired
    private LevelRepository levelRepository;

    private Level savedLevel;

    @BeforeEach
    public void beforeEach() {
        savedLevel = levelRepository.save(Level.builder()
                .levelName("등록된 난이도")
                .createdAt(LocalDateTime.now())
                .build()
        );
    }

    @Nested
    @DisplayName("난이도 등록 테스트")
    public class RegistrationTest {

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("유효한 요청의 경우")
            public void shouldReturnLevelDto_whenRequestIsValid() {
                // given
                String level = "등록할 난이도";
                LocalDateTime start = LocalDateTime.now();
                LevelAddRequest request = new LevelAddRequest(level);

                // when
                LevelDto result = levelService.addLevel(request);

                // then
                assertThat(result).isNotNull();
                assertThat(result.levelId()).isGreaterThan(0L);
                assertThat(result.levelName()).isEqualTo(level);
                assertThat(result.createdAt()).isBetween(start, LocalDateTime.now());
                assertThat(result.modifiedAt()).isNull();
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @Test
            @DisplayName("이미 사용중인 난이도 이름을 등록하려는 경우 - DuplicatedResourceException 발생")
            public void shouldThrowDuplicatedResourceException_whenLevelNameIsInUse() {
                // given
                String levelName = savedLevel.getLevelName();
                LevelAddRequest request = new LevelAddRequest(levelName);

                // when
                DuplicatedResourceException exception = assertThrows(
                        DuplicatedResourceException.class,
                        () -> levelService.addLevel(request)
                );

                assertThat(exception.getMessage()).isEqualTo("이미 사용중인 난이도입니다.");
            }
        }
    }

    @Nested
    @DisplayName("난이도 수정 테스트")
    public class UpdateTest {

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("유효한 요청인 경우")
            public void shouldReturnLevelDto_whenRequestIsValid() {
                // given
                long levelId = savedLevel.getId();
                String newLevelName = "수정된 난이도";
                LocalDateTime createdAt = savedLevel.getCreatedAt();
                LocalDateTime start = LocalDateTime.now();

                LevelUpdateRequest request = new LevelUpdateRequest(levelId, newLevelName);

                // when
                LevelDto result = levelService.updateLevel(request);

                // then
                assertThat(result).isNotNull();
                assertThat(result.levelId()).isEqualTo(levelId);
                assertThat(result.levelName()).isEqualTo(newLevelName);
                assertThat(result.createdAt()).isEqualTo(createdAt);
                assertThat(result.modifiedAt()).isBetween(start, LocalDateTime.now());
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @Test
            @DisplayName("이미 사용중인 난이도 이름으로 수정하려는 경우 - DuplicatedResourceException 예외 발생")
            public void shouldThrowDuplicatedResourceException_whenNewLevelNameIsDuplicated() {
                // given
                long levelId = savedLevel.getId();
                String newLevelName = "등록된 난이도";

                LevelUpdateRequest request = new LevelUpdateRequest(levelId, newLevelName);

                // when & then
                DuplicatedResourceException exception = assertThrows(
                        DuplicatedResourceException.class,
                        () -> levelService.updateLevel(request)
                );

                assertThat(exception.getMessage()).isEqualTo("이미 사용중인 난이도입니다.");
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "-1",
                    "0",
                    "9223372036854775807"
            })
            @DisplayName("이미 사용중인 난이도 이름으로 수정하려는 경우 - EntityNotFoundException 예외 발생")
            public void shouldThrowEntityNotFoundException_whenLevelDoesNotExist(long levelId) {
                // given
                String newLevelName = "수정할 난이도";

                LevelUpdateRequest request = new LevelUpdateRequest(levelId, newLevelName);

                // when & then
                EntityNotFoundException exception = assertThrows(
                        EntityNotFoundException.class,
                        () -> levelService.updateLevel(request)
                );

                assertThat(exception.getMessage()).isEqualTo("존재하지 않는 난이도입니다.");
            }
        }
    }

    @Nested
    @DisplayName("난이도 삭제 테스트")
    public class DeletionTest {

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("유효한 요청인 경우")
            public void shouldReturnLevelDto_whenRequestIsValid() {
                // given
                long levelId = savedLevel.getId();
                String levelName = savedLevel.getLevelName();
                LocalDateTime createdAt = savedLevel.getCreatedAt();
                LocalDateTime modifiedAt = savedLevel.getModifiedAt();

                // when
                LevelDto result = levelService.deleteLevel(levelId);

                // then
                assertThat(result).isNotNull();
                assertThat(result.levelId()).isEqualTo(levelId);
                assertThat(result.levelName()).isEqualTo(levelName);
                assertThat(result.createdAt()).isEqualTo(createdAt);
                assertThat(result.modifiedAt()).isEqualTo(modifiedAt);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @ParameterizedTest
            @CsvSource(value = {
                    "-1",
                    "0",
                    "9223372036854775807"
            })
            @DisplayName("존재하지 않는 난이도 ID를 사용하는 경우 - EntityNotFoundException 발생")
            public void shouldThrowEntityNotFoundException_whenLevelIdDoesNotExist(long levelId) {
                // given

                // when & then
                EntityNotFoundException exception = assertThrows(
                        EntityNotFoundException.class,
                        () -> levelService.deleteLevel(levelId)
                );

                assertThat(exception.getMessage()).isEqualTo("존재하지 않는 난이도입니다.");
            }
        }
    }
}