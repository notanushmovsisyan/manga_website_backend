CREATE TABLE authors (
                         id BIGSERIAL PRIMARY KEY,
                         author_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE INDEX idx_authors_name ON authors(author_name);