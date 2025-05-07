package com.upstage.devup.mypage.statistics.domain.entity;

import com.upstage.devup.auth.domain.entity.User;
import com.upstage.devup.question.domain.entity.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_answer_stats")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private int correctCount;

    @Column(nullable = false)
    private int wrongCount;

    @Column(nullable = false)
    private LocalDateTime first_solved_at;

    @Column(nullable = false)
    private LocalDateTime last_solved_at;

}
