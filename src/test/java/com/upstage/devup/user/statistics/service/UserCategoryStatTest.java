package com.upstage.devup.user.statistics.service;

import com.upstage.devup.user.statistics.dto.UserCategoryStatDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserCategoryStatTest {

    @Autowired
    private UserAnswerStatService userAnswerStatService;

    @Test
    @DisplayName("성공")
    public void success() {
        // given
        Long userId = 1L;

        // when
        UserCategoryStatDto results = userAnswerStatService.getUserCategoryStat(userId);

        // then
        assertThat(results).isNotNull();
        assertThat(results.getUserId()).isEqualTo(userId);
        assertThat(results.getTotalSolvedCount()).isGreaterThan(0);
        assertThat(results.getCategoryStats()).isNotEmpty();
    }
}
