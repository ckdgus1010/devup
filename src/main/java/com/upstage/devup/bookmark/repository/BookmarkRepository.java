package com.upstage.devup.bookmark.repository;

import com.upstage.devup.bookmark.dto.BookmarkDetails;
import com.upstage.devup.global.domain.id.BookmarkId;
import com.upstage.devup.global.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId> {
    boolean existsByUserIdAndQuestionId(long userId, long questionId);

    Optional<Bookmark> findByUserIdAndQuestionId(long userId, long questionId);

    @Query("""
        SELECT new com.upstage.devup.bookmark.dto.BookmarkDetails(
                q.id, q.title, c.categoryName, l.level, b.createdAt
                )
                FROM Bookmark b
                JOIN b.question q
                JOIN q.category c
                JOIN q.level l
                WHERE b.user.id = :userId
    """)
    Page<BookmarkDetails> findBookmarkDetailsByUserId(long userId, Pageable pageable);

}
