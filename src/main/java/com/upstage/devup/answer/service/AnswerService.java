package com.upstage.devup.answer.service;

import com.upstage.devup.answer.domain.dto.AnswerDetailDto;
import com.upstage.devup.answer.domain.entity.Answer;
import com.upstage.devup.answer.repository.AnswerRepository;
import com.upstage.devup.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    /**
     * 면접 질문 ID에 해당하는 정답 조회
     * @param questionId 면접 질문 ID
     * @return 조회된 정답
     * @throws IllegalArgumentException questionId가 null인 경우 발생
     * @throws EntityNotFoundException 조회 결과가 없는 경우 발생
     */
    public AnswerDetailDto getAnswerByQuestionId(Long questionId) {
        if (questionId == null) {
            throw new IllegalArgumentException("유효하지 않은 ID입니다.");
        }

        return answerRepository
                .findByQuestion_Id(questionId)
                .map(this::convertAnswerToAnswerDetailDto)
                .orElseThrow(() -> new EntityNotFoundException("정답을 찾을 수 없습니다."));
    }

    private AnswerDetailDto convertAnswerToAnswerDetailDto(Answer answer) {
        return AnswerDetailDto.builder()
                .id(answer.getId())
                .answerText(answer.getAnswerText())
                .questionId(answer.getQuestion().getId())
                .createdAt(answer.getCreatedAt())
                .modifiedAt(answer.getModifiedAt())
                .build();
    }
}
