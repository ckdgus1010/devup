package com.upstage.devup.ai.service;

import com.upstage.devup.ai.dto.AiAnswerCheckRequestDto;
import com.upstage.devup.ai.dto.AiAnswerCheckResponseDto;
import com.upstage.devup.answer.service.AnswerService;
import com.upstage.devup.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiAnswerCheckService {

    private final ChatClient chatClient;
    private final QuestionService questionService;
    private final AnswerService answerService;

    private static final String USER_MESSAGE_TEMPLATE = """
            1. 기술 면접 문제: %s
            2. 모범 답안: %s
            3. 사용자 답안: %s
            """;

    public AiAnswerCheckResponseDto checkUserAnswer(AiAnswerCheckRequestDto dto) {
        // 문제 조회
        String questionText = questionService.getQuestionText(dto.questionId());

        // 정답 조회
        String answerText = answerService.getAnswerText(dto.questionId());

        // user message 생성
        String userMessage = String.format(USER_MESSAGE_TEMPLATE, questionText, answerText, dto.userAnswer());

        // AI 에게 정답 판단 요청
        AiAnswerCheckResponseDto result = chatClient.prompt()
                .user(userMessage)
                .call()
                .entity(AiAnswerCheckResponseDto.class);

        // TODO: 오답 처리

        return result;
    }
}
