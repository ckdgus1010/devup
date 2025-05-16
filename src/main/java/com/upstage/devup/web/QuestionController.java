package com.upstage.devup.web;

import com.upstage.devup.question.dto.QuestionDetailDto;
import com.upstage.devup.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public String getQuestionListView(@RequestParam(defaultValue = "0") int pageNumber, Model model) {
        Page<QuestionDetailDto> questions = questionService.getQuestions(pageNumber);
        List<Integer> pageNumbers = getPageNumbers(questions);

        model.addAttribute("questions", questions);
        model.addAttribute("pageNumbers", pageNumbers);

        return "question/question-list";
    }

    private List<Integer> getPageNumbers(Page<QuestionDetailDto> questions) {
        int currentPage = questions.getNumber();
        int totalPages = questions.getTotalPages();

        int pageBlockSize = 5;
        int startPage = Math.max(0, currentPage - pageBlockSize / 2);
        int endPage = Math.min(totalPages, startPage + pageBlockSize);

        // startPage가 totalPages 가까이 있을 때 보정
        if (endPage - startPage < pageBlockSize && startPage > 0) {
            startPage = Math.max(0, endPage - pageBlockSize);
        }

        List<Integer> pageNumbers = IntStream.range(startPage, endPage).boxed().toList();
        return pageNumbers;
    }

    @GetMapping("/{questionId}")
    public String getQuestionDetailView(@PathVariable Long questionId, Model model) {
        QuestionDetailDto question = questionService.getQuestion(questionId);
        model.addAttribute("question", question);

        return "question/question-detail";
    }
}
