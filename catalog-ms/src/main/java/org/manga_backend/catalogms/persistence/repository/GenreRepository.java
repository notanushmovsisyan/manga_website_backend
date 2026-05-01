package org.manga_backend.catalogms.persistence.repository;

import org.manga_backend.catalogms.persistence.entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<GenreEntity, Long> {
    Optional<GenreEntity> findByGenreName(String genreName);
}