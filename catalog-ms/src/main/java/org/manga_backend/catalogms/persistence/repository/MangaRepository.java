package org.manga_backend.catalogms.persistence.repository;

import org.manga_backend.catalogms.persistence.entity.MangaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MangaRepository extends JpaRepository<MangaEntity, Long> {
}
