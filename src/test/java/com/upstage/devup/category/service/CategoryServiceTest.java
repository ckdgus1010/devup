package com.upstage.devup.category.service;

import com.upstage.devup.category.dto.CategoryAddRequest;
import com.upstage.devup.category.dto.CategoryDto;
import com.upstage.devup.category.dto.CategoryUpdateRequest;
import com.upstage.devup.category.repository.CategoryRepository;
import com.upstage.devup.global.entity.Category;
import com.upstage.devup.global.exception.DuplicatedResourceException;
import com.upstage.devup.global.exception.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CategoryServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    long savedCategoryId;
    String savedCategoryName;
    String savedColor;

    @BeforeEach
    public void beforeEach() {
        CategoryDto categoryDto = categoryService.addCategory(
                new CategoryAddRequest("old", "#000000")
        );

        this.savedCategoryId = categoryDto.id();
        this.savedCategoryName = categoryDto.categoryName();
        this.savedColor = categoryDto.color();
    }

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
                CategoryAddRequest request = new CategoryAddRequest(categoryName, savedColor);

                // when
                CategoryDto result = categoryService.addCategory(request);

                // then
                assertThat(result).isNotNull();
                assertThat(result.id()).isGreaterThan(0L);
                assertThat(result.categoryName()).isEqualTo(categoryName);
                assertThat(result.color()).isEqualTo(savedColor);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @Test
            @DisplayName("같은 categoryName을 저장하는 경우 - DuplicatedResourceException 발생")
            public void shouldThrowDuplicatedResourceException_whenCategoryNameExistsInDB() {
                // given
                String color = "#b0b0b0";
                CategoryAddRequest request = new CategoryAddRequest(savedCategoryName, color);

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

    @Nested
    @DisplayName("카테고리 수정 테스트")
    public class CategoryUpdateTest {

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("등록되지 않은 categoryName 값을 사용한 경우")
            public void shouldUpdateCategory() {
                // given
                String categoryName = "new category";
                String color = "#111111";
                CategoryUpdateRequest request = new CategoryUpdateRequest(savedCategoryId, categoryName, color);

                // when
                CategoryDto result = categoryService.updateCategory(request);

                // then
                assertThat(result).isNotNull();
                assertThat(result.id()).isEqualTo(savedCategoryId);
                assertThat(result.categoryName()).isEqualTo(categoryName);
                assertThat(result.color()).isEqualTo(color);
            }

            @Test
            @DisplayName("color 값이 중복되어도 categoryName이 다르면 수정할 수 있음")
            public void shouldUpdateCategory_whenColorIsDifferentButCategoryNameExistsInDB() {
                // given
                String categoryName = "new category";
                CategoryUpdateRequest request = new CategoryUpdateRequest(
                        savedCategoryId, categoryName, savedColor
                );

                // when
                CategoryDto result = categoryService.updateCategory(request);

                // then
                assertThat(result).isNotNull();
                assertThat(result.id()).isEqualTo(savedCategoryId);
                assertThat(result.categoryName()).isEqualTo(categoryName);
                assertThat(result.color()).isEqualTo(savedColor);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        public class FailureCases {

            @Test
            @DisplayName("중복된 categoryName 값을 사용하는 경우 - DuplicatedResourceException 발생")
            public void shouldThrowDuplicatedResourceException_whenCategoryNameExistsInDB() {
                // given
                String color = "#111111";
                CategoryUpdateRequest request = new CategoryUpdateRequest(savedCategoryId, savedCategoryName, color);

                // when
                DuplicatedResourceException exception = assertThrows(
                        DuplicatedResourceException.class,
                        () -> categoryService.updateCategory(request)
                );

                // then
                assertThat(exception.getMessage()).isEqualTo("이미 존재하는 카테고리입니다.");
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "0",
                    "-1",
                    "9223372036854775807"
            })
            @DisplayName("유효하지 않은 categoryId를 사용하는 경우 - EntityNotFoundException 발생")
            public void shouldThrowDEntityNotFoundException_whenCategoryEntityDoesNotExistsInDB(long categoryId) {
                // given
                String categoryName = "new category";
                String color = "#111111";
                CategoryUpdateRequest request = new CategoryUpdateRequest(categoryId, categoryName, color);

                // when
                EntityNotFoundException exception = assertThrows(
                        EntityNotFoundException.class,
                        () -> categoryService.updateCategory(request)
                );

                // then
                assertThat(exception.getMessage()).isEqualTo("존재하지 않는 카테고리입니다.");
            }
        }
    }

    @Nested
    @DisplayName("카테고리 삭제 테스트")
    public class CategoryDeleteTest {

        @Nested
        @DisplayName("성공 케이스")
        public class SuccessCases {

            @Test
            @DisplayName("외래키로 연결되지 않은 경우")
            public void shouldDeleteCategory() {
                // given
                Category category = categoryRepository.findById(savedCategoryId)
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않은 카테고리입니다."));

                // when
                CategoryDto result = categoryService.deleteCategory(category.getId());

                // then
                assertThat(result).isNotNull();
                assertThat(result.id()).isEqualTo(category.getId());
                assertThat(result.categoryName()).isEqualTo(category.getCategoryName());
                assertThat(result.color()).isEqualTo(category.getColor());
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
            @DisplayName("등록되지 않은 카테고리를 삭제하려는 경우")
            public void shouldThrowEntityNotFoundException_whenCategoryDoesNotExists(long categoryId) {
                // given

                // when & then
                EntityNotFoundException exception = assertThrows(
                        EntityNotFoundException.class,
                        () -> categoryService.deleteCategory(categoryId)
                );

                assertThat(exception.getMessage()).isEqualTo("존재하지 않는 카테고리입니다.");
            }
        }
    }
}