package com.upstage.devup.global.domain.id;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkId implements Serializable {

    private Long userId;
    private Long questionId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BookmarkId temp)) {
            return false;
        }

        return Objects.equals(userId, temp.userId) && Objects.equals(questionId, temp.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, questionId);
    }
}
