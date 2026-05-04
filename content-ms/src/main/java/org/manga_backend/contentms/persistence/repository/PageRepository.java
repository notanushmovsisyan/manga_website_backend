package org.manga_backend.contentms.persistence.repository;

import org.manga_backend.contentms.persistence.entity.ChapterPageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<ChapterPageEntity, Long> {
    Page<ChapterPageEntity> findByChapterId(Long chapterId, Pageable pageable);
    void deleteByChapterId(Long chapterId);
}