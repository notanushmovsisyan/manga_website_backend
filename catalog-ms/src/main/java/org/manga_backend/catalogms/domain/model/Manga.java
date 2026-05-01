package org.manga_backend.catalogms.domain.model;

import org.manga_backend.catalogms.persistence.Status;

import java.time.LocalDateTime;
import java.util.List;

public class Manga {
    private Long id;
    private String mangaTitle;
    private String coverImageUrl;
    private Status status;
    private List<Author> authors;
    private List<Genre> genres;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Manga() {}

    public Manga(Long id,
                 String mangaTitle,
                 String coverImageUrl,
                 Status status,
                 List<Author> authors,
                 List<Genre> genres,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt) {

        this.id = id;
        this.mangaTitle = mangaTitle;
        this.coverImageUrl = coverImageUrl;
        this.status = status;
        this.authors = authors;
        this.genres = genres;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMangaTitle() {
        return mangaTitle;
    }

    public void setMangaTitle(String mangaTitle) {
        this.mangaTitle = mangaTitle;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void addAuthor(Author author) {
        this.authors.add(author);
    }

    public void removeAuthor(Author author) {
        this.authors.remove(author);
    }

    public void addGenre(Genre genre) {
        this.genres.add(genre);
    }

    public void removeGenre(Genre genre) {
        this.genres.remove(genre);
    }
}
