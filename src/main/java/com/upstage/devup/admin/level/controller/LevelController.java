package com.upstage.devup.admin.level.controller;

import com.upstage.devup.admin.level.dto.LevelAddRequest;
import com.upstage.devup.admin.level.dto.LevelDto;
import com.upstage.devup.admin.level.dto.LevelPageDto;
import com.upstage.devup.admin.level.dto.LevelUpdateRequest;
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

    /**
     * 신규 난이도 등록
     *
     * @param request 등록할 난이도 정보
     * @return 등록된 난이도 정보
     */
    @PostMapping
    public ResponseEntity<LevelDto> addLevel(@RequestBody @Valid LevelAddRequest request) {

        return ResponseEntity.ok(
                levelService.addLevel(request)
        );
    }

    /**
     * 난이도 정보 수정
     *
     * @param request 수정할 난이도 정보
     * @return 수정된 난이도 정보
     */
    @PatchMapping
    public ResponseEntity<LevelDto> updateLevel(@RequestBody @Valid LevelUpdateRequest request) {

        return ResponseEntity.ok(
                levelService.updateLevel(request)
        );
    }

    // TODO: 삭제
}
