package org.manga_backend.contentms.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class PresignedUrlRequest {
    private Long mangaId;

    @NotNull
    @Positive
    private Double chapterNumber;

    @NotEmpty
    private List<String> originalFileNames;

    public PresignedUrlRequest() {}

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

    public List<String> getOriginalFileNames() {
        return originalFileNames;
    }

    public void setOriginalFileNames(List<String> originalFileNames) {
        this.originalFileNames = originalFileNames;
    }

    public void addFileName(String fileName) {
        originalFileNames.add(fileName);
    }

    public void removeFileName(String fileName) {
        originalFileNames.remove(fileName);
    }
}
