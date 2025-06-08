package com.upstage.devup.category.service;

import com.upstage.devup.category.dto.CategoryAddRequest;
import com.upstage.devup.category.dto.CategoryDto;
import com.upstage.devup.global.exception.DuplicatedResourceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Nested
    @DisplayName("카테고리 등록 테스트")
    public class CategoryRegistration {

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("등록되지 않은 categoryName을 저장하는 경우")
            public void shouldSaveCategory_whenCategoryNameDoesNotExistsInDB() {
                // given
                String categoryName = "new category";
                String color = "#b0b0b0";
                CategoryAddRequest request = new CategoryAddRequest(categoryName, color);

                // when
                CategoryDto result = categoryService.addCategory(request);

                // then
                assertThat(result).isNotNull();
                assertThat(result.id()).isGreaterThan(0L);
                assertThat(result.categoryName()).isEqualTo(categoryName);
                assertThat(result.color()).isEqualTo(color);
            }

            @Test
            @DisplayName("color 값이 중복되어도 categoryName이 다르면 저장할 수 있음")
            public void shouldSaveCategory_whenColorIsDuplicatedButNameIsDifferent() {
                // given
                String categoryName = "new category";
                String color = "#b0b0b0";
                categoryService.addCategory(new CategoryAddRequest("old category", color));

                CategoryAddRequest request = new CategoryAddRequest(categoryName, color);

                // when
                CategoryDto result = categoryService.addCategory(request);

                // then
                assertThat(result).isNotNull();
                assertThat(result.id()).isGreaterThan(0L);
                assertThat(result.categoryName()).isEqualTo(categoryName);
                assertThat(result.color()).isEqualTo(color);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @Test
            @DisplayName("같은 categoryName을 저장하는 경우 - DuplicatedResourceException 발생")
            public void shouldThrowDuplicatedResourceException_whenCategoryNameExistsInDB() {
                // given
                String categoryName = "new category";
                String color = "#b0b0b0";
                categoryService.addCategory(new CategoryAddRequest(categoryName, color));

                CategoryAddRequest request = new CategoryAddRequest(categoryName, color);

                // when
                DuplicatedResourceException exception = assertThrows(
                        DuplicatedResourceException.class,
                        () -> categoryService.addCategory(request)
                );

                // then
                assertThat(exception.getMessage()).isEqualTo("이미 존재하는 카테고리입니다.");
            }
        }
    }


}