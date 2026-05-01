package org.manga_backend.catalogms.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "authors")
public class AuthorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_name", unique = true, nullable = false)
    private String authorName;

    public AuthorEntity() {}

    public AuthorEntity(String authorName) {
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
