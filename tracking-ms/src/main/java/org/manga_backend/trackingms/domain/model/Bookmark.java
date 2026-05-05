package org.manga_backend.trackingms.domain.model;

import java.time.LocalDateTime;

public class Bookmark {
    private Long userId;
    private Long mangaId;
    private LocalDateTime createdAt;

    public Bookmark() {}

    public Bookmark(Long userId, Long mangaId, LocalDateTime createdAt) {
        this.userId = userId;
        this.mangaId = mangaId;
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMangaId() {
        return mangaId;
    }

    public void setMangaId(Long mangaId) {
        this.mangaId = mangaId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
