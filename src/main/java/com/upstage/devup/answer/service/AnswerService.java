package com.upstage.devup.answer.service;

import com.upstage.devup.answer.domain.dto.AnswerDetailDto;
import com.upstage.devup.answer.domain.entity.Answer;
import com.upstage.devup.answer.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    /**
     * 정답 조회
     * @param questionId 면접 질문 ID
     * @return 조회된 정답
     */
    public AnswerDetailDto getAnswerByQuestionId(Long questionId) {
        if (questionId == null) {
            return null;
        }

        return answerRepository
                .findByQuestion_Id(questionId)
                .map(this::convertAnswerToAnswerDetailDto)
                .orElse(null);
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
