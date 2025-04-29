package com.upstage.devup.question.service;

import com.upstage.devup.question.domain.dto.QuestionDetailDto;
import com.upstage.devup.question.domain.entity.Question;
import com.upstage.devup.question.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private static final int QUESTIONS_PER_PAGE = 10;

    /**
     * 면접 질문 목록 조회
     * @param page 페이지 번호
     * @return 조회된 면접 질문 목록
     */
    public List<QuestionDetailDto> getQuestions(int page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, QUESTIONS_PER_PAGE, sort);

        return boardRepository
                .findAll(pageable)
                .stream()
                .map(this::convertQuestionToDetailDto)
                .collect(Collectors.toList());
    }

    /**
     * 제목으로 면접 질문 목록 조회
     * @param title 검색할 제목
     * @param page 페이지 번호
     * @return 조회된 면접 질문 목록
     */
    public List<QuestionDetailDto> searchQuestionsByTitle(String title, int page) {
        if (title == null) {
            return List.of();
        }

        String str = title.trim();

        if (str.length() < 2) {
            return List.of();
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, QUESTIONS_PER_PAGE, sort);

        return boardRepository.findByTitleContainsIgnoreCase(str, pageable)
                .stream()
                .map(this::convertQuestionToDetailDto)
                .collect(Collectors.toList());
    }

    /**
     * 면접 질문 상세 조회
     * @param id 면접 질문 ID
     * @return 면접 질문 상세 정보
     */
    public QuestionDetailDto findById(Long id) {
        Optional<Question> optional = boardRepository.findById(id);

        if (optional.isEmpty()) {
            return null;
        }

        return convertQuestionToDetailDto(optional.get());
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
