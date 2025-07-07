package com.upstage.devup.admin.level.dto;

import com.upstage.devup.global.domain.dto.PageDto;
import org.springframework.data.domain.Page;

public class LevelPageDto extends PageDto<LevelDto> {

    public LevelPageDto(Page<LevelDto> page) {
        super(page);
    }
}
