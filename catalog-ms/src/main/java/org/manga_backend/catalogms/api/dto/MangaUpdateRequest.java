package org.manga_backend.catalogms.api.dto;

import org.manga_backend.catalogms.persistence.Status;

public class MangaUpdateRequest {
    private String mangaTitle;
    private String coverImageUrl;
    private Status status;

    public String getMangaTitle() {
        return mangaTitle;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public Status getStatus() {
        return status;
    }

    public void setMangaTitle(String mangaTitle) {
        this.mangaTitle = mangaTitle;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
