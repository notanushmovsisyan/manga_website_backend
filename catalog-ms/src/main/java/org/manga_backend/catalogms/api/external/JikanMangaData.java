package org.manga_backend.catalogms.api.external;

import java.util.List;

public class JikanMangaData {
    Long malId;
    String title;
    JikanImages images;
    String status;
    List<JikanAuthor> authors;
    List<JikanGenre> genres;

    public Long getMalId() {
        return malId;
    }

    public String getTitle() {
        return title;
    }

    public JikanImages getImages() {
        return images;
    }

    public String getStatus() {
        return status;
    }

    public List<JikanAuthor> getAuthors() {
        return authors;
    }

    public List<JikanGenre> getGenres() {
        return genres;
    }
}
