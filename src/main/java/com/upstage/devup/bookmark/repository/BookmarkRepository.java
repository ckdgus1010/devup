package com.upstage.devup.bookmark.repository;

import com.upstage.devup.global.domain.id.BookmarkId;
import com.upstage.devup.global.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId> {
    boolean existsByUserIdAndQuestionId(long userId, long questionId);

    Optional<Bookmark> findByUserIdAndQuestionId(long userId, long questionId);
}
