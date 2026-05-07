CREATE TABLE mangas (
                        id BIGSERIAL PRIMARY KEY,

                        manga_title VARCHAR(255) NOT NULL UNIQUE,

                        cover_image_url TEXT NOT NULL UNIQUE,

                        status VARCHAR(50),

                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_mangas_title ON mangas(manga_title);