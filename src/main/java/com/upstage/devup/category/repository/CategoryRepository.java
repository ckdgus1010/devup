package com.upstage.devup.category.repository;

import com.upstage.devup.global.entity.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByCategory(@NotBlank(message = "카테고리를 입력해 주세요.") String category);
}
