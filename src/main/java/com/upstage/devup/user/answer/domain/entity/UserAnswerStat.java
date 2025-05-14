package com.upstage.devup.user.answer.domain.entity;

import com.upstage.devup.auth.domain.entity.User;
import com.upstage.devup.question.domain.entity.Question;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private Integer correctCount;

    @Column(nullable = false)
    private Integer wrongCount;

    @Column(nullable = false)
    private LocalDateTime firstSolvedAt;

    @Setter
    @Column(nullable = false)
    private LocalDateTime lastSolvedAt;

    public void increaseCorrectCount() {
        this.correctCount += 1;
    }

    public void increaseWrongCount() {
        this.wrongCount += 1;
    }

}
