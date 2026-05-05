package org.manga_backend.trackingms.api.dto;

import java.time.LocalDateTime;

public class ReadingProgressDto {
    private Long userId;
    private Long mangaId;
    private Long lastReadChapterId;
    private Integer lastReadChapterNumber;
    private LocalDateTime updatedAt;

    public ReadingProgressDto(Long userId,
                           Long mangaId,
                           Long lastReadChapterId,
                           Integer lastReadChapterNumber) {

        this.userId = userId;
        this.mangaId = mangaId;
        this.lastReadChapterId = lastReadChapterId;
        this.lastReadChapterNumber = lastReadChapterNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getMangaId() {
        return mangaId;
    }

    public Long getLastReadChapterId() {
        return lastReadChapterId;
    }

    public Integer getLastReadChapterNumber() {
        return lastReadChapterNumber;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
