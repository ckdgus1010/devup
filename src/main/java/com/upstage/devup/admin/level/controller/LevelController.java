package com.upstage.devup.admin.level.controller;

import com.upstage.devup.admin.level.dto.LevelDto;
import com.upstage.devup.admin.level.dto.LevelPageDto;
import com.upstage.devup.admin.level.service.LevelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    /**
     * 난이도 단건 조회
     *
     * @param levelId 조회할 난이도 ID
     * @return 조회된 난이도 정보
     */
    @GetMapping("/{levelId}")
    public ResponseEntity<LevelDto> getLevel(@PathVariable @Valid Long levelId) {

        return ResponseEntity.ok(
                levelService.getLevel(levelId)
        );
    }


    /**
     * 난이도 페이지 조회
     *
     * @param pageNumber 조회할 페이지 번호
     * @return 조회된 난이도 페이지 정보
     */
    @GetMapping
    public ResponseEntity<LevelPageDto> getLevels(@RequestParam @Valid Integer pageNumber) {

        return ResponseEntity.ok(
                levelService.getLevels(pageNumber)
        );
    }

    // TODO: 등록
    // TODO: 수정
    // TODO: 삭제
}
