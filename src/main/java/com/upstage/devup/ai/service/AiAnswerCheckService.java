package com.upstage.devup.ai.service;

import com.upstage.devup.ai.dto.AiAnswerCheckRequestDto;
import com.upstage.devup.ai.dto.AiAnswerCheckResponseDto;
import com.upstage.devup.ai.dto.OpenAiAnswerCheckResult;
import com.upstage.devup.answer.service.AnswerService;
import com.upstage.devup.question.service.QuestionService;
import com.upstage.devup.user.answer.dto.UserAnswerSaveRequest;
import com.upstage.devup.user.answer.service.UserAnswerSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiAnswerCheckService {

    private final ChatClient chatClient;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserAnswerSaveService userAnswerSaveService;

    private static final String USER_MESSAGE_TEMPLATE = """
            1. 기술 면접 문제: %s
            2. 사용자 답안: %s
            """;

    public AiAnswerCheckResponseDto checkUserAnswer(long userId, AiAnswerCheckRequestDto dto) {
        // 문제 및 모범 답안 조회
        String questionText = questionService.getQuestionText(dto.questionId());
        String modelAnswer = answerService.getAnswerText(dto.questionId());

        // user message 생성
        String userMessage = String.format(USER_MESSAGE_TEMPLATE, questionText, dto.userAnswer());

        // AI 에게 정답 판단 요청
        OpenAiAnswerCheckResult result = chatClient.prompt()
                .user(userMessage)
                .call()
                .entity(OpenAiAnswerCheckResult.class);

        if (result == null) {
            // TODO: 예외 처리
            return null;
        }

        // 사용자가 제출한 답안 저장
        userAnswerSaveService.saveUserAnswer(
                userId,
                new UserAnswerSaveRequest(dto.questionId(), modelAnswer, result.isCorrect())
        );

        return new AiAnswerCheckResponseDto(result.isCorrect(), result.feedback(), modelAnswer);
    }
}
