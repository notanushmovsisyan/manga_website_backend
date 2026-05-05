package org.manga_backend.trackingms.api.controller;

import org.manga_backend.trackingms.api.dto.BookmarkRequest;
import org.manga_backend.trackingms.api.dto.BookmarkResponse;
import org.manga_backend.trackingms.api.dto.PagedResponse;
import org.manga_backend.trackingms.api.dto.ReadingProgressDto;
import org.manga_backend.trackingms.api.mapper.BookmarkMapper;
import org.manga_backend.trackingms.api.mapper.ReadingProgressMapper;
import org.manga_backend.trackingms.domain.model.Bookmark;
import org.manga_backend.trackingms.domain.model.ReadingProgress;
import org.manga_backend.trackingms.domain.service.TrackingService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("#userId.toString() == authentication.name")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping("/users/{userId}/bookmarks")
    public ResponseEntity<PagedResponse<BookmarkResponse>> getBookmarksByUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        PagedResponse<Bookmark> pagedBookmarks = trackingService.getBookmarksByUserId(userId, pageable);

        List<BookmarkResponse> dtos = pagedBookmarks.content().stream()
                .map(BookmarkMapper::bookmarkToBookmarkResponse)
                .toList();

        return ResponseEntity.ok(new PagedResponse<>(
                dtos,
                pagedBookmarks.pageNumber(),
                pagedBookmarks.pageSize(),
                pagedBookmarks.totalElements(),
                pagedBookmarks.last()
        ));
    }

    @PostMapping("/users/{userId}/bookmarks")
    public ResponseEntity<Void> addBookmark(
            @PathVariable Long userId,
            @RequestBody BookmarkRequest request) {

        trackingService.saveBookmark(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/users/{userId}/bookmarks")
    public ResponseEntity<Void> deleteBookmark(
            @PathVariable Long userId,
            @RequestBody BookmarkRequest request) {

        trackingService.deleteBookmark(userId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/history")
    public ResponseEntity<PagedResponse<ReadingProgressDto>> getUserHistory(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "updatedAt") Pageable pageable) {

        PagedResponse<ReadingProgress> pagedHistory = trackingService.getUserHistory(userId, pageable);

        List<ReadingProgressDto> dtos = pagedHistory.content()
                .stream()
                .map(ReadingProgressMapper::readingProgressToDto)
                .toList();

        // Fixed return type to PagedResponse<ReadingProgressDto>
        return ResponseEntity.ok(new PagedResponse<>(
                dtos,
                pagedHistory.pageNumber(),
                pagedHistory.pageSize(),
                pagedHistory.totalElements(),
                pagedHistory.last()
        ));
    }

    @PatchMapping("/users/{userId}/history")
    public ResponseEntity<Void> updateUserHistory(
            @PathVariable Long userId,
            @RequestBody ReadingProgressDto progressDto) {

        trackingService.updateReadingProgress(userId, progressDto);
        return ResponseEntity.noContent().build();
    }
}