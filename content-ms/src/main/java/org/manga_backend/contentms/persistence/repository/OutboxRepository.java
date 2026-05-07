package org.manga_backend.contentms.persistence.repository;

import org.manga_backend.contentms.persistence.entity.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {
    List<OutboxEntity> findTop100ByPublishedFalseOrderByCreatedAtAsc();
}