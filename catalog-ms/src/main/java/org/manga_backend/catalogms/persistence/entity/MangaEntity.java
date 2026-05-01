package org.manga_backend.catalogms.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.manga_backend.catalogms.persistence.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mangas")
public class MangaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "manga_title", unique = true, nullable = false)
    private String mangaTitle;

    @Column(name = "cover_image_url", unique = true, nullable = false)
    private String coverImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private Status status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "mangas_authors",
            joinColumns = @JoinColumn(name = "manga_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<AuthorEntity> authors = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "mangas_genres",
            joinColumns = @JoinColumn(name = "manga_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<GenreEntity> genres = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public MangaEntity() {}

    public MangaEntity(String mangaTitle, String coverImageUrl, Status status) {
        this.mangaTitle = mangaTitle;
        this.coverImageUrl = coverImageUrl;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getMangaTitle() {
        return mangaTitle;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<AuthorEntity> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorEntity> authors) {
        this.authors = authors;
    }

    public List<GenreEntity> getGenres() {
        return genres;
    }

    public void setGenres(List<GenreEntity> genres) {
        this.genres = genres;
    }

    public void addAuthor(AuthorEntity author) {
        this.authors.add(author);
    }

    public void removeAuthor(AuthorEntity author) {
        this.authors.remove(author);
    }

    public void addGenre(GenreEntity genre) {
        this.genres.add(genre);
    }

    public void removeGenre(GenreEntity genre) {
        this.genres.remove(genre);
    }
}
