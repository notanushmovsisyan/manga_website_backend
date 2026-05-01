package org.manga_backend.catalogms.api;

public class MangaCreationRequest {
    private Long externalId;
    private String source;

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
