package org.manga_backend.contentms.domain.model;

import java.time.LocalDateTime;

public class Chapter {
    private Long id;
    private Long mangaId;
    private Double chapterNumber;
    private String chapterTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Chapter(Long mangaId, Double chapterNumber, String chapterTitle) {
        this.mangaId = mangaId;
        this.chapterNumber = chapterNumber;
        this.chapterTitle = chapterTitle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMangaId() {
        return mangaId;
    }

    public void setMangaId(Long mangaId) {
        this.mangaId = mangaId;
    }

    public Double getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(Double chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
