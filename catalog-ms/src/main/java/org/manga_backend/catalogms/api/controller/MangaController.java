package org.manga_backend.catalogms.api.controller;

import org.manga_backend.catalogms.api.dto.MangaCreationRequest;
import org.manga_backend.catalogms.domain.model.Manga;
import org.manga_backend.catalogms.domain.service.MangaImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            try {
                Manga savedManga = importService.importFromMal(request.getExternalId());
                return ResponseEntity.ok(savedManga);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        return ResponseEntity.badRequest().body("Manual creation not yet implemented");
    }
}
