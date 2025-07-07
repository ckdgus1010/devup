package com.upstage.devup.user.wrong.controller;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.user.wrong.service.UserWrongNoteDeleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wrong")
@RequiredArgsConstructor
public class UserWrongNoteDeleteController {

    private final UserWrongNoteDeleteService userWrongNoteDeleteService;

    /**
     * 오답노트 삭제
     *
     * @param user       로그인한 사용자 정보
     * @param questionId 삭제할 오답노트의 질문 ID
     * @throws EntityNotFoundException 조회된 오답노트가 없는 경우 발생
     */
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteWrongNote(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long questionId
    ) {
        userWrongNoteDeleteService.deleteWrongNote(user.userId(), questionId);
        return ResponseEntity.ok().build();
    }

}
