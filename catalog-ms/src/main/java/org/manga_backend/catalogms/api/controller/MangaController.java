package org.manga_backend.catalogms.api.controller;

import org.manga_backend.catalogms.api.dto.MangaCreationRequest;
import org.manga_backend.catalogms.api.dto.MangaResponse;
import org.manga_backend.catalogms.api.dto.MangaUpdateRequest;
import org.manga_backend.catalogms.api.mapper.MangaMapper;
import org.manga_backend.catalogms.domain.model.Manga;
import org.manga_backend.catalogms.domain.service.MangaImportService;
import org.manga_backend.catalogms.domain.service.MangaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mangas")
public class MangaController {
    private final MangaImportService importService;
    private final MangaService mangaService;

    public MangaController(MangaImportService importService, MangaService mangaService) {
        this.importService = importService;
        this.mangaService = mangaService;
    }

    @PreAuthorize("hasRole('ROLE_UPLOADER')")
    @PostMapping
    public ResponseEntity<?> createManga(@RequestBody MangaCreationRequest request) {

        if (request.getExternalId() != null && "MAL".equalsIgnoreCase(request.getSource())) {
            Manga savedManga = importService.importFromMal(request.getExternalId());
            MangaResponse responseDto = MangaMapper.mangaToMangaResponse(savedManga);
            return ResponseEntity.ok(responseDto);
        }

        return ResponseEntity.badRequest().body(Map.of("error", "Manual creation not yet implemented"));
    }

    @GetMapping
    public ResponseEntity<List<MangaResponse>> getAllManga() {

        List<MangaResponse> mangaResponseList = mangaService.getAllMangas()
                .stream()
                .map(MangaMapper::mangaToMangaResponse)
                .toList();

        return ResponseEntity.ok(mangaResponseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MangaResponse> getMangaById(@PathVariable Long id) {
        MangaResponse response = MangaMapper.mangaToMangaResponse(mangaService.getMangaById(id));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_UPLOADER')")
    @PutMapping("/{id}")
    public ResponseEntity<MangaResponse> updateMangaById(@PathVariable Long id,
                                                         @RequestBody MangaUpdateRequest request) {

        Manga updatedManga = mangaService.updateMangaById(id, request);
        MangaResponse response = MangaMapper.mangaToMangaResponse(updatedManga);
        return ResponseEntity.ok(response);
    }
}
