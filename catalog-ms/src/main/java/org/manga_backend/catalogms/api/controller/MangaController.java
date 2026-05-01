package org.manga_backend.catalogms.api.controller;

import org.manga_backend.catalogms.api.dto.MangaCreationRequest;
import org.manga_backend.catalogms.api.dto.MangaResponse;
import org.manga_backend.catalogms.api.mapper.MangaMapper;
import org.manga_backend.catalogms.domain.model.Manga;
import org.manga_backend.catalogms.domain.service.MangaImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/mangas")
public class MangaController {
    private final MangaImportService importService;

    public MangaController(MangaImportService importService) {
        this.importService = importService;
    }

    @PreAuthorize("hasRole('UPLOADER')")
    @PostMapping
    public ResponseEntity<?> createManga(@RequestBody MangaCreationRequest request) {
        if (request.getExternalId() != null && "MAL".equalsIgnoreCase(request.getSource())) {
            Manga savedManga = importService.importFromMal(request.getExternalId());
            MangaResponse responseDto = MangaMapper.mangaToMangaResponse(savedManga);

            return ResponseEntity.ok(responseDto);
        }

        return ResponseEntity.badRequest().body(Map.of("error", "Manual creation not yet implemented"));    }
}
