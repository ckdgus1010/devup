package com.upstage.devup.answer.service;

import com.upstage.devup.answer.domain.dto.AnswerDetailDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AnswerServiceTest {

    @Autowired
    private AnswerService answerService;

    @Transactional
    @Test
    @DisplayName("정답 조회 성공")
    public void successToFindAnswer() {
        // given
        Long[] questionIds = new Long[]{1L, 10L, 30L};
        String[] answerTexts = new String[]{
                "OOP는 캡슐화, 상속, 다형성, 추상화의 네 가지 원칙을 포함합니다.",
                "JPA는 자바에서 객체와 관계형 데이터베이스의 데이터를 매핑하기 위한 API로, 데이터베이스 작업을 객체 지향적으로 처리할 수 있게 해줍니다.",
                "정규 표현식은 특정 패턴의 문자열을 찾거나 교체하는데 사용되는 언어로, 자주 사용하는 패턴으로는 \\d, \\w, \\s 등이 있습니다."
        };

        for (int i = 0; i < questionIds.length; i++) {
            // when
            Long questionId = questionIds[i];
            String answerText = answerTexts[i];
            AnswerDetailDto result = answerService.getAnswerByQuestionId(questionId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getQuestionId()).isEqualTo(questionId);
            assertThat(result.getAnswerText()).isEqualTo(answerText);
        }
    }

    @Test
    @DisplayName("정답 조회 - null로 정답을 조회하면 실패")
    public void failToFindAnswerByNull() {
        // given
        Long questionId = null;

        // when
        AnswerDetailDto result = answerService.getAnswerByQuestionId(questionId);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("정답 조회 - 유효하지 않은 questionId로 정답을 조회하면 실패")
    public void fainToFindAnswerByUnavailableQuestionId() {
        // given
        Long questionId = 0L;

        // when
        AnswerDetailDto result = answerService.getAnswerByQuestionId(questionId);

        // then
        assertThat(result).isNull();
    }

}