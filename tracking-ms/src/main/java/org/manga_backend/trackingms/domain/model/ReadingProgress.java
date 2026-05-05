package org.manga_backend.trackingms.domain.model;

import java.time.LocalDateTime;

public class ReadingProgress {
    private Long userId;
    private Long mangaId;
    private Long lastReadChapterId;
    private Integer lastReadChapterNumber;
    private LocalDateTime updatedAt;

    public ReadingProgress() {}

    public ReadingProgress(Long userId,
                           Long mangaId,
                           Long lastReadChapterId,
                           Integer lastReadChapterNumber,
                           LocalDateTime updatedAt) {

        this.userId = userId;
        this.mangaId = mangaId;
        this.lastReadChapterId = lastReadChapterId;
        this.lastReadChapterNumber = lastReadChapterNumber;
        this.updatedAt = updatedAt;
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

    public Long getLastReadChapterId() {
        return lastReadChapterId;
    }

    public void setLastReadChapterId(Long lastReadChapterId) {
        this.lastReadChapterId = lastReadChapterId;
    }

    public Integer getLastReadChapterNumber() {
        return lastReadChapterNumber;
    }

    public void setLastReadChapterNumber(Integer lastReadChapterNumber) {
        this.lastReadChapterNumber = lastReadChapterNumber;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}