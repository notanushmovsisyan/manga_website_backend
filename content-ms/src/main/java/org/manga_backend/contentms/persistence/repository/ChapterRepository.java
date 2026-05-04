package org.manga_backend.contentms.persistence.repository;

import org.manga_backend.contentms.persistence.entity.ChapterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterRepository extends JpaRepository<ChapterEntity, Long> {
    Page<ChapterEntity> findByMangaId(Long mangaId, Pageable pageable);
}
