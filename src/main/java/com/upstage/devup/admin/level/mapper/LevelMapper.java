package com.upstage.devup.admin.level.mapper;

import com.upstage.devup.admin.level.dto.LevelDto;
import com.upstage.devup.global.entity.Level;
import org.springframework.stereotype.Component;

@Component
public class LevelMapper {

    private LevelMapper() {
    }

    public LevelDto toLevelDto(Level entity) {
        return new LevelDto(
                entity.getId(),
                entity.getLevelName(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
