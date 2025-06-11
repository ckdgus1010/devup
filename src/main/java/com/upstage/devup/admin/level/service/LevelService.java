package com.upstage.devup.admin.level.service;

import com.upstage.devup.admin.level.dto.LevelAddRequest;
import com.upstage.devup.admin.level.dto.LevelDto;
import com.upstage.devup.admin.level.dto.LevelUpdateRequest;
import com.upstage.devup.admin.level.mapper.LevelMapper;
import com.upstage.devup.admin.level.repository.LevelRepository;
import com.upstage.devup.global.entity.Level;
import com.upstage.devup.global.exception.DuplicatedResourceException;
import com.upstage.devup.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LevelService {

    private final LevelRepository levelRepository;

    private final LevelMapper levelMapper;

    private static final String ERR_MSG_DUPLICATED_RESOURCE = "이미 사용중인 난이도입니다.";
    private static final String ERR_MSG_ENTITY_NOT_FOUND = "존재하지 않는 난이도입니다.";

    /**
     * 난이도 단건 조회
     *
     * @param levelId 조회할 난이도 ID
     * @return 조회된 난이도 정보
     * @throws EntityNotFoundException 존재하지 않는 난이도 ID를 사용하는 경우 발생
     */
    public LevelDto getLevel(long levelId) {

        return levelMapper.toLevelDto(
                levelRepository.findById(levelId)
                        .orElseThrow(() -> new EntityNotFoundException(ERR_MSG_ENTITY_NOT_FOUND))
        );
    }

    // TODO: 난이도 목록 조회


    /**
     * 신규 난이도 등록
     *
     * @param request 새로 등록할 난이도 정보
     * @return 등록된 난이도 정보
     * @throws DuplicatedResourceException 중복된 난이도를 등록하려는 경우 발생
     */
    @Transactional
    public LevelDto addLevel(LevelAddRequest request) {

        if (isLevelNameDuplicated(request.levelName())) {
            throw new DuplicatedResourceException(ERR_MSG_DUPLICATED_RESOURCE);
        }

        Level entity = levelRepository.save(Level.builder()
                .levelName(request.levelName())
                .createdAt(LocalDateTime.now())
                .build());

        return levelMapper.toLevelDto(entity);
    }

    /**
     * 난이도 수정
     *
     * @param request 수정할 난이도 정보
     * @return 수정된 난이도 정보
     * @throws DuplicatedResourceException 이미 사용중인 난이도 이름을 사용한 경우 발생
     * @throws EntityNotFoundException     존재하지 않는 난이도 ID를 사용한 경우 발생
     */
    @Transactional
    public LevelDto updateLevel(LevelUpdateRequest request) {

        if (isLevelNameDuplicated(request.levelName())) {
            throw new DuplicatedResourceException(ERR_MSG_DUPLICATED_RESOURCE);
        }

        Level entity = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new EntityNotFoundException(ERR_MSG_ENTITY_NOT_FOUND));

        entity.setLevelName(request.levelName());

        return levelMapper.toLevelDto(entity);
    }

    /**
     * 난이도 삭제
     *
     * @param levelId 삭제할 난이도 ID
     * @return 삭제된 난이도 정보
     * @throws EntityNotFoundException         존재하지 않는 난이도 ID를 사용한 경우 발생
     * @throws DataIntegrityViolationException 삭제하려는 난이도가 외래키로 사용중인 경우 발생
     */
    @Transactional
    public LevelDto deleteLevel(long levelId) {

        Level entity = levelRepository.findById(levelId)
                .orElseThrow(() -> new EntityNotFoundException(ERR_MSG_ENTITY_NOT_FOUND));

        try {
            LevelDto result = levelMapper.toLevelDto(entity);
            levelRepository.delete(entity);

            log.info("난이도 삭제 ::: levelId = {}", levelId);

            return result;
        } catch (DataIntegrityViolationException e) {
            log.warn("삭제 실패 ::: levelId = {}, 해당 Level은 하나 이상의 Question에서 참조 중입니다.", levelId);
            throw new DataIntegrityViolationException("삭제할 수 없습니다. 다른 데이터에서 이 항목을 사용 중입니다.");
        }
    }

    /**
     * 난이도 이름 중복 검사
     *
     * @param levelName 중복 검사할 난이도 이름
     * @return true: 중복됨, false: 중복되지 않음
     */
    private boolean isLevelNameDuplicated(String levelName) {
        return levelRepository.existsByLevelName(levelName);
    }
}
