package com.upstage.devup.question.service;

import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.question.domain.dto.QuestionDetailDto;
import com.upstage.devup.question.domain.entity.Question;
import com.upstage.devup.question.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private static final int QUESTIONS_PER_PAGE = 10;

    /**
     * 면접 질문 목록 조회
     *
     * @param page 페이지 번호
     * @return 조회된 면접 질문 목록
     */
    public Page<QuestionDetailDto> getQuestions(int page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, QUESTIONS_PER_PAGE, sort);

        return boardRepository
                .findAll(pageable)
                .map(this::convertQuestionToDetailDto);
    }

    /**
     * 제목으로 면접 질문 목록 조회
     *
     * @param title 검색할 제목
     * @param page  페이지 번호
     * @return 조회된 면접 질문 목록
     */
    public Page<QuestionDetailDto> searchQuestionsByTitle(String title, int page) {
        if (title == null) {
            return Page.empty();
        }

        String str = title.trim();

        if (str.length() < 2) {
            return Page.empty();
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, QUESTIONS_PER_PAGE, sort);

        return boardRepository
                .findByTitleContainsIgnoreCase(str, pageable)
                .map(this::convertQuestionToDetailDto);
    }

    /**
     * 면접 질문 상세 조회
     *
     * @param id 면접 질문 ID
     * @return 면접 질문 상세 정보
     */
    public QuestionDetailDto getQuestion(Long id) {
        return boardRepository
                .findById(id)
                .map(this::convertQuestionToDetailDto)
                .orElseThrow(() -> new EntityNotFoundException("면접 질문을 찾을 수 없습니다."));
    }

    /**
     * 면접 질문 ID가 사용 중인지 확인
     *
     * @param questionId 확인할 면접 질문 ID
     * @return 사용 중인 ID, false: 사용하지 않는 ID
     */
    public boolean isQuestionIdInUse(Long questionId) {
        return boardRepository.existsById(questionId);
    }

    private QuestionDetailDto convertQuestionToDetailDto(Question question) {
        return QuestionDetailDto.builder()
                .id(question.getId())
                .title(question.getTitle())
                .questionText(question.getQuestionText())
                .category(question.getCategory().getCategory())
                .level(question.getLevel().getLevel())
                .createdAt(question.getCreatedAt())
                .modifiedAt(question.getModifiedAt())
                .build();
    }

}
