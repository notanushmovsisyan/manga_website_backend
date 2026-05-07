package org.manga_backend.notifms.api.dto;

import java.time.Instant;

public record ChapterPublishedEvent(
        Long mangaId,
        Long chapterId,
        Double chapterNumber,
        Instant timestamp
) {}
