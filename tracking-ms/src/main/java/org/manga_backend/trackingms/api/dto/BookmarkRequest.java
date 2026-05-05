package org.manga_backend.trackingms.api.dto;

public record BookmarkRequest(
        Long userId,
        Long mangaId) {}
