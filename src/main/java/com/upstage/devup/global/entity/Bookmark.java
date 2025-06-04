package com.upstage.devup.global.entity;

import com.upstage.devup.global.domain.id.BookmarkId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
public class Bookmark {

    @EmbeddedId
    private BookmarkId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("questionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Bookmark() {}

    public Bookmark(User user, Question question, LocalDateTime createdAt) {
        this.id = new BookmarkId(user.getId(), question.getId());
        this.user = user;
        this.question = question;
        this.createdAt = createdAt;
    }

    public Bookmark(BookmarkId id, User user, Question question, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.question = question;
        this.createdAt = createdAt;
    }
}
