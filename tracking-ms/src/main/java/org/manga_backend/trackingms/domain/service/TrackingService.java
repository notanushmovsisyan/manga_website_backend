package org.manga_backend.trackingms.domain.service;

import org.manga_backend.trackingms.api.dto.BookmarkRequest;
import org.manga_backend.trackingms.api.dto.PagedResponse;
import org.manga_backend.trackingms.api.dto.ReadingProgressDto;
import org.manga_backend.trackingms.domain.model.Bookmark;
import org.manga_backend.trackingms.domain.model.ReadingProgress;
import org.manga_backend.trackingms.persistence.TrackingEntity;
import org.manga_backend.trackingms.persistence.TrackingPersistenceMapper;
import org.manga_backend.trackingms.persistence.TrackingRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrackingService {
    private final TrackingRepository trackingRepository;

    public TrackingService(TrackingRepository trackingRepository) {
        this.trackingRepository = trackingRepository;
    }

    public PagedResponse<Bookmark> getBookmarksByUserId(Long userId, Pageable pageable) {
        List<TrackingEntity> entities = trackingRepository.findBookmarksByUserId(userId);

        // Manual pagination for DynamoDB results to satisfy the Spring Pageable interface
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), entities.size());

        List<Bookmark> content = entities.subList(start, end).stream()
                .map(TrackingPersistenceMapper::toBookmark)
                .toList();

        org.springframework.data.domain.Page<Bookmark> page = new org.springframework.data.domain.PageImpl<>(
                content, pageable, entities.size()
        );

        return new PagedResponse<>(page);
    }

    public void saveBookmark(Long userId, BookmarkRequest request) {
        TrackingEntity existing = trackingRepository
                .findByUserIdAndMangaId(userId, request.mangaId())
                .orElse(null);

        Bookmark bookmark = new Bookmark(userId, request.mangaId(), LocalDateTime.now());
        TrackingEntity entity = TrackingPersistenceMapper.updateEntity(existing, bookmark, null);
        trackingRepository.save(entity);
    }

    public PagedResponse<ReadingProgress> getUserHistory(Long userId, Pageable pageable) {
        List<TrackingEntity> entities = trackingRepository.findHistoryByUserId(userId);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), entities.size());

        List<ReadingProgress> content = entities.subList(start, end).stream()
                .map(TrackingPersistenceMapper::toReadingProgress)
                .filter(java.util.Objects::nonNull)
                .toList();

        org.springframework.data.domain.Page<ReadingProgress> page = new org.springframework.data.domain.PageImpl<>(
                content, pageable, entities.size()
        );

        return new PagedResponse<>(page);
    }

    public void updateReadingProgress(Long userId, ReadingProgressDto dto) {
        // 1. Try to find if this user already has a record for this manga
        TrackingEntity existing = trackingRepository
                .findByUserIdAndMangaId(userId, dto.getMangaId())
                .orElse(null);

        // 2. Create a domain model from the DTO
        ReadingProgress progress = new ReadingProgress(
                userId,
                dto.getMangaId(),
                dto.getLastReadChapterId(),
                dto.getLastReadChapterNumber(),
                LocalDateTime.now() // Set the update time to 'now'
        );

        // 3. Use the mapper to merge the new progress into the existing record
        // We pass 'null' for Bookmark because we only want to update progress here
        TrackingEntity entity = TrackingPersistenceMapper.updateEntity(existing, null, progress);

        // 4. Save the merged entity back to DynamoDB
        trackingRepository.save(entity);
    }
}
