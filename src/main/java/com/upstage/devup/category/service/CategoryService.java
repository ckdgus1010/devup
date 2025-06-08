package com.upstage.devup.category.service;

import com.upstage.devup.category.dto.CategoryAddRequest;
import com.upstage.devup.category.dto.CategoryDto;
import com.upstage.devup.category.mapper.CategoryMapper;
import com.upstage.devup.category.repository.CategoryRepository;
import com.upstage.devup.global.exception.DuplicatedResourceException;
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

    // 카테고리 저장

    /**
     * 카테고리 저장
     *
     * @param request 저장할 카테고리 정보
     * @return 저장된 카테고리 정보
     */
    @Transactional
    public CategoryDto addCategory(CategoryAddRequest request) {
        if (categoryRepository.existsByCategory(request.category())) {
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

    // TODO: 카테고리 수정
    // TODO: 카테고리 삭제
}
