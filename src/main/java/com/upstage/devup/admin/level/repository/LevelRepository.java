package com.upstage.devup.admin.level.repository;

import com.upstage.devup.global.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
    boolean existsByLevelName(String levelName);
}
