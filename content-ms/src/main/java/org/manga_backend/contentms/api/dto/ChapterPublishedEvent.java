package org.manga_backend.contentms.api.dto;

import java.time.Instant;

public record ChapterPublishedEvent(
        Long mangaId,
        Long chapterId,
        Double chapterNumber,
        Instant occurredAt
) {}
