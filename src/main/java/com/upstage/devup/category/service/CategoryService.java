package com.upstage.devup.category.service;

import com.upstage.devup.category.dto.CategoryAddRequest;
import com.upstage.devup.category.dto.CategoryDto;
import com.upstage.devup.category.dto.CategoryUpdateRequest;
import com.upstage.devup.category.mapper.CategoryMapper;
import com.upstage.devup.category.repository.CategoryRepository;
import com.upstage.devup.global.entity.Category;
import com.upstage.devup.global.exception.DuplicatedResourceException;
import com.upstage.devup.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * 카테고리 저장
     *
     * @param request 저장할 카테고리 정보
     * @return 저장된 카테고리 정보
     */
    @Transactional
    public CategoryDto addCategory(CategoryAddRequest request) {
        if (checkCategoryNameIsInUse(request.category())) {
            throw new DuplicatedResourceException("이미 존재하는 카테고리입니다.");
        }

        try {
            return categoryMapper.toCategoryDto(
                    categoryRepository.save(
                            categoryMapper.toEntity(request)
                    )
            );
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicatedResourceException("이미 존재하는 카테고리입니다.");
        }
    }

    /**
     * 카테고리 수정하기
     *
     * @param request 수정할 카테고리 정보
     * @return 수정된 카테고리 정보
     * @throws DuplicatedResourceException 중복된 카테고리 이름을 사용하는 경우 발생
     * @throws EntityNotFoundException     수정하려는 카테고리가 없는 경우
     */
    @Transactional
    public CategoryDto updateCategory(CategoryUpdateRequest request) {
        if (checkCategoryNameIsInUse(request.category())) {
            throw new DuplicatedResourceException("이미 존재하는 카테고리입니다.");
        }

        Category entity = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카테고리입니다."));

        entity.setCategoryName(request.category());
        entity.setColor(request.color());

        try {
            return categoryMapper.toCategoryDto(
                    categoryRepository.save(entity)
            );
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicatedResourceException("이미 존재하는 카테고리입니다.");
        }
    }

    // TODO: 카테고리 삭제

    /**
     * 카테고리 명 중복 확인
     *
     * @param categoryName 중복 검사할 카테고리 이름
     * @return true: 중복 값 있음, false: 중복 값 없음
     */
    private boolean checkCategoryNameIsInUse(String categoryName) {
        return categoryRepository.existsByCategoryName(categoryName);
    }
}
