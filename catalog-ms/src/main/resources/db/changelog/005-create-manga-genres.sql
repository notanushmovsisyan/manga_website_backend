CREATE TABLE mangas_genres (
                               manga_id BIGINT NOT NULL,
                               genre_id BIGINT NOT NULL,

                               PRIMARY KEY (manga_id, genre_id),

                               CONSTRAINT fk_manga_genre_manga
                                   FOREIGN KEY (manga_id)
                                       REFERENCES mangas(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_manga_genre_genre
                                   FOREIGN KEY (genre_id)
                                       REFERENCES genres(id)
                                       ON DELETE CASCADE
);

CREATE INDEX idx_mangas_genres_manga ON mangas_genres(manga_id);
CREATE INDEX idx_mangas_genres_genre ON mangas_genres(genre_id);