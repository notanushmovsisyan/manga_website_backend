CREATE TABLE mangas_authors (
                                manga_id BIGINT NOT NULL,
                                author_id BIGINT NOT NULL,

                                PRIMARY KEY (manga_id, author_id),

                                CONSTRAINT fk_manga_author_manga
                                    FOREIGN KEY (manga_id)
                                        REFERENCES mangas(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT fk_manga_author_author
                                    FOREIGN KEY (author_id)
                                        REFERENCES authors(id)
                                        ON DELETE CASCADE
);

CREATE INDEX idx_mangas_authors_manga ON mangas_authors(manga_id);
CREATE INDEX idx_mangas_authors_author ON mangas_authors(author_id);