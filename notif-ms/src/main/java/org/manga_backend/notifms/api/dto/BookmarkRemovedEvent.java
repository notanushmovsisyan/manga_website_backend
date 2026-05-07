package org.manga_backend.notifms.api.dto;

import java.time.Instant;

public record BookmarkRemovedEvent(Long userId, Long mangaId, Instant timestamp) {}