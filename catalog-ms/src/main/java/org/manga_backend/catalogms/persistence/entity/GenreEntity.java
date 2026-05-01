package org.manga_backend.catalogms.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "genres")
public class GenreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "genre_name", unique = true, nullable = false)
    private String genreName;

    public GenreEntity() {}

    public GenreEntity(String name) {
        this.genreName = name;
    }

    public Long getId() {
        return id;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}
