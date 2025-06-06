package com.upstage.devup.question.service;

import com.upstage.devup.bookmark.service.BookmarkService;
import com.upstage.devup.global.entity.Question;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.question.dto.QuestionDetailDto;
import com.upstage.devup.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService {

    private final BookmarkService bookmarkService;
    private final QuestionRepository questionRepository;

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

        return questionRepository
                .findAll(pageable)
                .map(entity -> convertQuestionToDetailDto(entity, false));
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

        return questionRepository
                .findByTitleContainsIgnoreCase(str, pageable)
                .map(entity -> convertQuestionToDetailDto(entity, false));
    }

    /**
     * 면접 질문 상세 정보 조회
     *
     * @param userId     사용자 ID
     * @param questionId 면접 질문 ID
     * @return 조회된 면접 질문 상세 정보
     */
    public QuestionDetailDto getQuestion(long userId, long questionId) {

        boolean isBookmarked = bookmarkService.checkBookmarkIsRegistered(userId, questionId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("면접 질문을 찾을 수 없습니다."));

        return convertQuestionToDetailDto(question, isBookmarked);
    }

    private QuestionDetailDto convertQuestionToDetailDto(Question question, boolean isBookmarked) {
        return QuestionDetailDto.builder()
                .id(question.getId())
                .title(question.getTitle())
                .questionText(question.getQuestionText())
                .category(question.getCategory().getCategory())
                .level(question.getLevel().getLevel())
                .isBookmarked(isBookmarked)
                .createdAt(question.getCreatedAt())
                .modifiedAt(question.getModifiedAt())
                .build();
    }

}
