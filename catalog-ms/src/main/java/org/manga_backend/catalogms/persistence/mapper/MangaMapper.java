package org.manga_backend.catalogms.persistence.mapper;

import org.manga_backend.catalogms.domain.model.Author;
import org.manga_backend.catalogms.domain.model.Genre;
import org.manga_backend.catalogms.domain.model.Manga;
import org.manga_backend.catalogms.persistence.entity.AuthorEntity;
import org.manga_backend.catalogms.persistence.entity.GenreEntity;
import org.manga_backend.catalogms.persistence.entity.MangaEntity;

import java.util.List;

public class MangaMapper {
    public static MangaEntity mangaToMangaEntity(Manga manga) {
        MangaEntity entity = new MangaEntity(manga.getMangaTitle(), manga.getCoverImageUrl(), manga.getStatus());

        if (manga.getAuthors() != null) {
            List<AuthorEntity> authorEntities = manga.getAuthors().stream()
                    .map(AuthorMapper::authorToAuthorEntity)
                    .toList();

            entity.setAuthors(authorEntities);
        }

        if (manga.getGenres() != null) {
            List<GenreEntity> genreEntities = manga.getGenres().stream()
                    .map(GenreMapper::genreToGenreEntity)
                    .toList();

            entity.setGenres(genreEntities);
        }

        return entity;
    }

    public static Manga mangaEntityToManga(MangaEntity entity) {
        List<Author> authors = new java.util.ArrayList<>();
        if (entity.getAuthors() != null) {
            authors = entity.getAuthors().stream()
                    .map(AuthorMapper::authorEntityToAuthor)
                    .toList();
        }

        List<Genre> genres = new java.util.ArrayList<>();
        if (entity.getGenres() != null) {
            genres = entity.getGenres().stream()
                    .map(GenreMapper::genreEntityToGenre)
                    .toList();
        }

        return new Manga(
                entity.getId(),
                entity.getMangaTitle(),
                entity.getCoverImageUrl(),
                entity.getStatus(),
                authors,
                genres,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
