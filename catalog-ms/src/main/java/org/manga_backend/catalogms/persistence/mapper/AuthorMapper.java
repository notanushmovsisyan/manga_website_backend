package org.manga_backend.catalogms.persistence.mapper;

import org.manga_backend.catalogms.domain.model.Author;
import org.manga_backend.catalogms.persistence.entity.AuthorEntity;

public class AuthorMapper {
    public static AuthorEntity authorToAuthorEntity(Author author) {
        return new AuthorEntity(author.getAuthorName());
    }

    public static Author authorEntityToAuthor(AuthorEntity entity) {
        return new Author(entity.getId(), entity.getAuthorName());
    }
}
