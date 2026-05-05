package org.manga_backend.trackingms.api.dto;

import java.time.LocalDateTime;

public record BookmarkResponse(
        Long userId,
        Long mangaId,
        LocalDateTime createdAt) {
}
