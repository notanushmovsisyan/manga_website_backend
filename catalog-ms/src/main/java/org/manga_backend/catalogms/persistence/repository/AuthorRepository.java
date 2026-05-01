package org.manga_backend.catalogms.persistence.repository;

import org.manga_backend.catalogms.persistence.entity.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {
    Optional<AuthorEntity> findByAuthorName(String authorName);
}
