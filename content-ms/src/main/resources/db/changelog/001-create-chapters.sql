CREATE TABLE chapters (
                          id BIGSERIAL PRIMARY KEY,

                          manga_id BIGINT NOT NULL,

                          chapter_number DOUBLE PRECISION NOT NULL,

                          chapter_title VARCHAR(255) NOT NULL,

                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_chapters_manga_id
    ON chapters(manga_id);

CREATE INDEX idx_chapters_manga_chapter
    ON chapters(manga_id, chapter_number);