package com.upstage.devup.category.mapper;

import com.upstage.devup.category.dto.CategoryAddRequest;
import com.upstage.devup.category.dto.CategoryDto;
import com.upstage.devup.global.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    private CategoryMapper() {
    }

    public Category toEntity(CategoryAddRequest request) {
        return Category.builder()
                .category(request.category())
                .color(request.color())
                .build();
    }

    public CategoryDto toCategoryDto(Category entity) {
        return new CategoryDto(
                entity.getId(),
                entity.getCategory(),
                entity.getColor()
        );
    }
}
