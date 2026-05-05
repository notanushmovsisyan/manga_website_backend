package org.manga_backend.trackingms.api.dto;

import java.time.Instant;

public record BookmarkAddedEvent(
        Long userId,
        Long mangaId,
        Instant occurredAt
) {}
