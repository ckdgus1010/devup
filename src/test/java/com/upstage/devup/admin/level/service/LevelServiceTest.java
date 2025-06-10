package com.upstage.devup.admin.level.service;

import com.upstage.devup.admin.level.dto.LevelAddRequest;
import com.upstage.devup.admin.level.dto.LevelDto;
import com.upstage.devup.admin.level.repository.LevelRepository;
import com.upstage.devup.global.entity.Level;
import com.upstage.devup.global.exception.DuplicatedResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
}