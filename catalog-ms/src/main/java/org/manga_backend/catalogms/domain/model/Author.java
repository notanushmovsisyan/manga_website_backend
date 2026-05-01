package org.manga_backend.catalogms.domain.model;

public class Author {
    private Long id;
    private String authorName;

    public Author() {}

    public Author(Long id, String authorName) {
        this.id = id;
        this.authorName = authorName;
    }

    public Long getId() {
        return id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
