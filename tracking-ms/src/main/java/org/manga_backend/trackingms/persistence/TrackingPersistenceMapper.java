package org.manga_backend.trackingms.persistence;

import org.manga_backend.trackingms.domain.model.Bookmark;
import org.manga_backend.trackingms.domain.model.ReadingProgress;

public class TrackingPersistenceMapper {
    public static TrackingEntity updateEntity(TrackingEntity existing, Bookmark bm, ReadingProgress rp) {
        // If we have an existing record, use it. Otherwise, start fresh.
        TrackingEntity entity = (existing != null) ? existing : new TrackingEntity();

        // If we are processing a Bookmark update
        if (bm != null) {
            entity.setUserId(bm.getUserId());
            entity.setMangaId(bm.getMangaId());
            entity.setBookmarked(true);
            entity.setUpdatedAt(bm.getCreatedAt());
        }

        // If we are processing a Progress update
        if (rp != null) {
            entity.setUserId(rp.getUserId());
            entity.setMangaId(rp.getMangaId());
            entity.setLastReadChapterId(rp.getLastReadChapterId());
            entity.setLastReadChapterNumber(rp.getLastReadChapterNumber());
            entity.setUpdatedAt(rp.getUpdatedAt());

            // Important: If this is a brand new record, it's not bookmarked yet
            if (entity.getBookmarked() == null) {
                entity.setBookmarked(false);
            }
        }

        return entity;
    }

    public static TrackingEntity toTrackingEntity(Bookmark bm, ReadingProgress rp) {
        TrackingEntity entity = new TrackingEntity();

        if (bm != null) {
            entity.setUserId(bm.getUserId());
            entity.setMangaId(bm.getMangaId());
            entity.setBookmarked(true);
            entity.setUpdatedAt(bm.getCreatedAt());
        }

        if (rp != null) {
            entity.setUserId(rp.getUserId());
            entity.setMangaId(rp.getMangaId());
            entity.setLastReadChapterId(rp.getLastReadChapterId());
            entity.setLastReadChapterNumber(rp.getLastReadChapterNumber());
            entity.setUpdatedAt(rp.getUpdatedAt());

            if (entity.getBookmarked() == null) {
                entity.setBookmarked(false);
            }
        }

        return entity;
    }

    public static Bookmark toBookmark(TrackingEntity entity) {
        if (entity == null || !Boolean.TRUE.equals(entity.getBookmarked())) {
            return null;
        }

        return new Bookmark(entity.getUserId(), entity.getMangaId(), entity.getUpdatedAt());
    }

    public static ReadingProgress toReadingProgress(TrackingEntity entity) {
        if (entity == null || entity.getLastReadChapterId() == null) {
            return null;
        }

        return new ReadingProgress(
                entity.getUserId(),
                entity.getMangaId(),
                entity.getLastReadChapterId(),
                entity.getLastReadChapterNumber(),
                entity.getUpdatedAt()
        );
    }
}
