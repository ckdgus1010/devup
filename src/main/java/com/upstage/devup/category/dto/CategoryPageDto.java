package com.upstage.devup.category.dto;

import com.upstage.devup.global.domain.dto.PageDto;
import org.springframework.data.domain.Page;

public class CategoryPageDto extends PageDto<CategoryDto> {

    public CategoryPageDto(Page<CategoryDto> page) {
        super(page);
    }
}
