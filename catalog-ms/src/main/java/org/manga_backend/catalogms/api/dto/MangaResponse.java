package org.manga_backend.catalogms.api.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MangaResponse(
        Long id,
        String mangaTitle,
        String coverImageUrl,
        String status,
        List<AuthorResponse> authors,
        List<GenreResponse> genres,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}


