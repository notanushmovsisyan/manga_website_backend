CREATE TABLE pages (
                       id BIGSERIAL PRIMARY KEY,

                       chapter_id BIGINT NOT NULL,

                       page_number INTEGER NOT NULL,

                       s3_url TEXT NOT NULL,

                       CONSTRAINT fk_pages_chapter
                           FOREIGN KEY (chapter_id)
                               REFERENCES chapters(id)
                               ON DELETE CASCADE,

                       CONSTRAINT uk_chapter_page_number
                           UNIQUE (chapter_id, page_number)
);

CREATE INDEX idx_pages_chapter_id
    ON pages(chapter_id);

CREATE INDEX idx_pages_chapter_id
    ON pages(chapter_id);