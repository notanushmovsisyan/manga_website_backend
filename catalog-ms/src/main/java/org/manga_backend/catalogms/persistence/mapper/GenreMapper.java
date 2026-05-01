package org.manga_backend.catalogms.persistence.mapper;

import org.manga_backend.catalogms.domain.model.Genre;
import org.manga_backend.catalogms.persistence.entity.GenreEntity;

public class GenreMapper {
    public static GenreEntity genreToGenreEntity(Genre genre) {
        return new GenreEntity(genre.getGenreName());
    }

    public static Genre genreEntityToGenre(GenreEntity entity) {
        return new Genre(entity.getId(), entity.getGenreName());
    }
}
