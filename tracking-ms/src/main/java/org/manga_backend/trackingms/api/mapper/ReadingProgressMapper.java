package org.manga_backend.trackingms.api.mapper;

import org.manga_backend.trackingms.api.dto.ReadingProgressDto;
import org.manga_backend.trackingms.domain.model.ReadingProgress;

public class ReadingProgressMapper {
    public static ReadingProgress dtoToReadingProgress(ReadingProgressDto dto) {
        ReadingProgress progress = new ReadingProgress();
        progress.setUserId(dto.getUserId());
        progress.setMangaId(dto.getMangaId());
        progress.setLastReadChapterNumber(dto.getLastReadChapterNumber());
        progress.setLastReadChapterId(dto.getLastReadChapterId());
        return progress;
    }

    public static ReadingProgressDto readingProgressToDto(ReadingProgress progress) {
        ReadingProgressDto dto = new ReadingProgressDto(progress.getUserId(),
                progress.getMangaId(),
                progress.getLastReadChapterId(),
                progress.getLastReadChapterNumber());

        dto.setUpdatedAt(progress.getUpdatedAt());
        return dto;
    }
}
