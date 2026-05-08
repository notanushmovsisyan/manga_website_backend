package org.manga_backend.contentms.domain.service;

import io.micrometer.core.instrument.Counter;
import org.manga_backend.contentms.api.dto.ChapterPublishedEvent;
import org.manga_backend.contentms.api.dto.OutboxPayload;
import org.manga_backend.contentms.api.dto.PagedResponse;
import org.manga_backend.contentms.api.exception.ChapterNotFoundException;
import org.manga_backend.contentms.domain.model.Chapter;
import org.manga_backend.contentms.domain.model.ChapterPage;
import org.manga_backend.contentms.persistence.entity.ChapterEntity;
import org.manga_backend.contentms.persistence.entity.ChapterPageEntity;
import org.manga_backend.contentms.persistence.entity.OutboxEntity;
import org.manga_backend.contentms.persistence.mapper.ChapterMapper;
import org.manga_backend.contentms.persistence.mapper.PageMapper;
import org.manga_backend.contentms.persistence.repository.ChapterRepository;
import org.manga_backend.contentms.persistence.repository.OutboxRepository;
import org.manga_backend.contentms.persistence.repository.PageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.core.instrument.MeterRegistry;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final PageRepository pageRepository;
    private final S3StorageServiceImpl storageService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final Counter chaptersUploadedCounter;

    private static final Logger log = LoggerFactory.getLogger(ChapterService.class);

    public ChapterService(ChapterRepository chapterRepository,
                          PageRepository pageRepository,
                          S3StorageServiceImpl storageService,
                          OutboxRepository outboxRepository,
                          ObjectMapper objectMapper,
                          MeterRegistry meterRegistry) {

        this.chapterRepository = chapterRepository;
        this.pageRepository = pageRepository;
        this.storageService = storageService;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.chaptersUploadedCounter = Counter.builder("business.chapters.uploaded.total")
                .description("Total number of manga chapters successfully uploaded")
                .register(meterRegistry);
    }

    @Transactional
    public void addPagesToChapter(Long chapterId, List<ChapterPage> chapterPages) {
        ChapterEntity chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> {
                    log.warn("Add pages failed: chapter not found id={}", chapterId);
                    return new ChapterNotFoundException("Chapter not found");
                });

        List<ChapterPageEntity> pageEntities = chapterPages.stream()
                .map(chapterPage -> {
                    ChapterPageEntity entity = PageMapper.pageToPageEntity(chapterPage);
                    entity.setChapterId(chapterId); // Link to chapter
                    return entity;
                })
                .toList();

        pageRepository.saveAll(pageEntities);
        log.info("Pages saved successfully chapterId={} count={}", chapterId, pageEntities.size());
    }

    @Transactional
    public Chapter createChapter(Chapter chapter) {
        ChapterEntity entity = ChapterMapper.chapterToChapterEntity(chapter);
        ChapterEntity savedEntity = chapterRepository.save(entity);
        log.info("Chapter created successfully id={}", savedEntity.getId());
        return ChapterMapper.chapterEntityToChapter(savedEntity);
    }

    @Transactional
    public void confirmUpload(Long chapterId, List<ChapterPage> chapterPages) {

        if (pageRepository.existsByChapterId(chapterId)) {
            log.warn("Upload already confirmed chapterId={}", chapterId);
            throw new IllegalStateException("Chapter upload already confirmed");
        }

        ChapterEntity chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> {
                    log.warn("Confirm upload failed: chapter not found id={}", chapterId);
                    return new ChapterNotFoundException("Chapter not found");
                });

        // =========================
        // VALIDATION
        // =========================

        if (chapterPages == null || chapterPages.isEmpty()) {
            throw new IllegalArgumentException("Chapter pages cannot be empty");
        }

        Set<Integer> seenPageNumbers = new HashSet<>();

        List<Integer> sortedPageNumbers = chapterPages.stream()
                .map(ChapterPage::getPageNumber)
                .sorted()
                .toList();

        for (ChapterPage page : chapterPages) {

            if (page.getPageNumber() == null || page.getPageNumber() <= 0) {
                log.warn("Invalid page number chapterId={} pageNumber={}", chapterId, page.getPageNumber());
                throw new IllegalArgumentException("Invalid page number");
            }

            if (page.getS3Url() == null || page.getS3Url().isBlank()) {
                log.warn("Missing S3 URL chapterId={} pageNumber={}", chapterId, page.getPageNumber());
                throw new IllegalArgumentException("S3 URL cannot be empty");
            }

            // duplicate page numbers
            if (!seenPageNumbers.add(page.getPageNumber())) {
                log.warn("Duplicate page number detected chapterId={} pageNumber={}", chapterId, page.getPageNumber());
                throw new IllegalArgumentException("Duplicate page number: " + page.getPageNumber());
            }
        }

        // check sequential ordering (1,2,3,4...)
        for (int i = 0; i < sortedPageNumbers.size(); i++) {

            int expected = i + 1;
            int actual = sortedPageNumbers.get(i);

            if (actual != expected) {
                log.warn("Page ordering mismatch chapterId={} expected={} actual={}", chapterId, expected, actual);
                throw new IllegalArgumentException("Missing or invalid page ordering at page " + expected);
            }
        }

        // =========================
        // SAVE PAGES
        // =========================

        List<ChapterPageEntity> entities = chapterPages.stream()
                .map(p -> {
                    ChapterPageEntity entity = PageMapper.pageToPageEntity(p);
                    entity.setChapterId(chapterId);
                    return entity;
                })
                .toList();

        pageRepository.saveAll(entities);
        log.info("Chapter pages saved successfully chapterId={}", chapterId);

        // =========================
        // CREATE OUTBOX EVENT
        // =========================

        ChapterPublishedEvent event = new ChapterPublishedEvent(
                chapter.getMangaId(),
                chapter.getId(),
                chapter.getChapterNumber(),
                Instant.now()
        );

        try {

            OutboxPayload outboxPayload = new OutboxPayload(
                    java.util.UUID.randomUUID(),
                    "CHAPTER_PUBLISHED",
                    "CHAPTER",
                    chapter.getId(),
                    Instant.now(),
                    event
            );

            String payload = objectMapper.writeValueAsString(outboxPayload);

            OutboxEntity outbox = new OutboxEntity(
                    "CHAPTER",
                    chapter.getId(),
                    "CHAPTER_PUBLISHED",
                    payload
            );

            outboxRepository.save(outbox);
            chaptersUploadedCounter.increment();
            log.info("Outbox event saved chapterId={} eventType={}", chapterId, outbox.getEventType());

        } catch (Exception e) {
            log.error("Failed to create outbox event chapterId={}", chapterId, e);
            throw new RuntimeException(e);
        }
    }

    public Chapter getChapterById(Long id) {
        return chapterRepository.findById(id)
                .map(ChapterMapper::chapterEntityToChapter)
                .orElseThrow(() -> new ChapterNotFoundException("Chapter not found with id: " + id));
    }

    public List<Chapter> getChaptersByMangaId(Long mangaId, Pageable pageable) {
        return chapterRepository.findByMangaId(mangaId, pageable)
                .stream()
                .map(ChapterMapper::chapterEntityToChapter)
                .toList();
    }

    public PagedResponse<ChapterPage> getPagesByChapterId(Long chapterId, Pageable pageable) {
        Page<ChapterPageEntity> entity = pageRepository.findByChapterId(chapterId, pageable);
        Page<ChapterPage> chapterPages = entity.map(PageMapper::pageEntityToPage);
        log.debug("Pages fetched chapterId={} count={}", chapterId, entity.getTotalElements());
        return new PagedResponse<>(chapterPages);
    }

    @Transactional
    public void deleteChapter(Long id) {
        ChapterEntity entity = chapterRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Delete failed. Chapter not found id={}", id);
                    return new ChapterNotFoundException("Chapter not found");
                });

        String s3FolderPath = String.format("mangas/%d/chapters/%d/", entity.getMangaId(), entity.getId());

        try {
            storageService.deleteFolder(s3FolderPath);
        } catch (Exception e) {
            log.error("Failed to delete S3 folder for chapterId={} path={}", id, s3FolderPath, e);
        }

        pageRepository.deleteByChapterId(id);
        chapterRepository.delete(entity);
        log.info("Chapter deleted successfully id={}", id);
    }
}
