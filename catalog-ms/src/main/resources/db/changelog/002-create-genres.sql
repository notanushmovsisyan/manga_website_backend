CREATE TABLE genres (
                        id BIGSERIAL PRIMARY KEY,
                        genre_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE INDEX idx_genres_name ON genres(genre_name);