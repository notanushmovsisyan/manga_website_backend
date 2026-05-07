package org.manga_backend.contentms.api.controller;

import org.manga_backend.contentms.api.dto.PagedResponse;
import org.manga_backend.contentms.api.dto.PresignedUrlRequest;
import org.manga_backend.contentms.api.dto.PresignedUrlResponse;
import org.manga_backend.contentms.domain.model.Chapter;
import org.manga_backend.contentms.domain.model.ChapterPage;
import org.manga_backend.contentms.domain.service.ChapterService;
import org.manga_backend.contentms.domain.service.S3StorageServiceImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ChaptersController {
    private final ChapterService chapterService;
    private final S3StorageServiceImpl storageService;

    public ChaptersController(ChapterService chapterService, S3StorageServiceImpl storageService) {
        this.chapterService = chapterService;
        this.storageService = storageService;
    }

    @GetMapping("/mangas/{mangaId}/chapters")
    public ResponseEntity<List<Chapter>> getAllChaptersByMangaId(
            @PathVariable Long mangaId,
            @PageableDefault(size = 10, sort = "chapterNumber") Pageable pageable) {

        List<Chapter> chapters = chapterService.getChaptersByMangaId(mangaId, pageable);
        return ResponseEntity.ok(chapters);
    }

    @PreAuthorize("hasRole('ROLE_UPLOADER')")
    @PostMapping("/mangas/{mangaId}/chapters")
    public ResponseEntity<List<PresignedUrlResponse>> createChapterAndGetUploadUrls(
            @PathVariable Long mangaId,
            @Valid @RequestBody PresignedUrlRequest request) {

        Chapter newChapter = new Chapter(mangaId,
                request.getChapterNumber(),
                "Chapter " + request.getChapterNumber()
        );

        Chapter savedChapter = chapterService.createChapter(newChapter);
        List<PresignedUrlResponse> urls = storageService.generateUploadUrls(mangaId, savedChapter.getId(), request);
        return ResponseEntity.ok(urls);
    }

    @PreAuthorize("hasRole('ROLE_UPLOADER')")
    @PostMapping("/chapters/{chapterId}/confirm")
    public ResponseEntity<Void> confirmUpload(@PathVariable Long chapterId,
                                              @RequestBody List<ChapterPage> chapterPages) {

        chapterService.confirmUpload(chapterId, chapterPages);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/chapters/{chapterId}")
    public ResponseEntity<Chapter> getChapter(@PathVariable Long chapterId) {
        return ResponseEntity.ok(chapterService.getChapterById(chapterId));
    }

    @GetMapping("/chapters/{chapterId}/pages")
    public ResponseEntity<PagedResponse<ChapterPage>> getChapterPages(
            @PathVariable Long chapterId,
            @PageableDefault(size = 3, sort = "pageNumber") Pageable pageable) {

        return ResponseEntity.ok(chapterService.getPagesByChapterId(chapterId, pageable));
    }

    @PreAuthorize("hasRole('ROLE_UPLOADER')")
    @DeleteMapping("/chapters/{chapterId}")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long chapterId) {
        chapterService.deleteChapter(chapterId);
        return ResponseEntity.noContent().build();
    }
}
