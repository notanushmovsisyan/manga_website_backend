package org.manga_backend.catalogms.api.mapper;

import org.manga_backend.catalogms.api.dto.AuthorResponse;
import org.manga_backend.catalogms.api.dto.GenreResponse;
import org.manga_backend.catalogms.api.dto.MangaResponse;
import org.manga_backend.catalogms.domain.model.Manga;

import java.util.List;

public class MangaMapper {
    public static MangaResponse mangaToMangaResponse(Manga manga) {
        if (manga == null) return null;

        List<AuthorResponse> authorResponses = new java.util.ArrayList<>();
        if (manga.getAuthors() != null) {
            authorResponses = manga.getAuthors().stream()
                    .map(a -> new AuthorResponse(a.getId(), a.getAuthorName()))
                    .toList();
        }

        List<GenreResponse> genreResponses = new java.util.ArrayList<>();
        if (manga.getGenres() != null) {
            genreResponses = manga.getGenres().stream()
                    .map(g -> new GenreResponse(g.getId(), g.getGenreName()))
                    .toList();
        }

        return new MangaResponse(
                manga.getId(),
                manga.getMangaTitle(),
                manga.getCoverImageUrl(),
                manga.getStatus() != null ? manga.getStatus().name() : null,
                authorResponses,
                genreResponses,
                manga.getCreatedAt(),
                manga.getUpdatedAt()
        );
    }
}
