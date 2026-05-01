package org.manga_backend.catalogms.api.external;

public class JikanResponse {
    private JikanMangaData data;

    public JikanResponse() {}

    public JikanResponse(JikanMangaData data) {
        this.data = data;
    }

    public JikanMangaData getData() {
        return data;
    }
}
