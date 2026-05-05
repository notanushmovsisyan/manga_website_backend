package org.manga_backend.contentms.domain.service;

import org.manga_backend.contentms.api.dto.PagedResponse;
import org.manga_backend.contentms.api.exception.ChapterNotFoundException;
import org.manga_backend.contentms.domain.model.Chapter;
import org.manga_backend.contentms.domain.model.ChapterPage;
import org.manga_backend.contentms.persistence.entity.ChapterEntity;
import org.manga_backend.contentms.persistence.entity.ChapterPageEntity;
import org.manga_backend.contentms.persistence.mapper.ChapterMapper;
import org.manga_backend.contentms.persistence.mapper.PageMapper;
import org.manga_backend.contentms.persistence.repository.ChapterRepository;
import org.manga_backend.contentms.persistence.repository.PageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final PageRepository pageRepository;
    private final S3StorageServiceImpl storageService;
    private final ChapterPublisherService publisherService;

    public ChapterService(ChapterRepository chapterRepository,
                          PageRepository pageRepository,
                          S3StorageServiceImpl storageService,
                          ChapterPublisherService publisherService) {

        this.chapterRepository = chapterRepository;
        this.pageRepository = pageRepository;
        this.storageService = storageService;
        this.publisherService = publisherService;
    }

    @Transactional
    public void addPagesToChapter(Long chapterId, List<ChapterPage> chapterPages) {
        ChapterEntity chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ChapterNotFoundException("Chapter not found"));

        List<ChapterPageEntity> pageEntities = chapterPages.stream()
                .map(chapterPage -> {
                    ChapterPageEntity entity = PageMapper.pageToPageEntity(chapterPage);
                    entity.setChapterId(chapterId); // Link to chapter
                    return entity;
                })
                .toList();

        pageRepository.saveAll(pageEntities);
    }

    @Transactional
    public Chapter createChapter(Chapter chapter) {
        ChapterEntity entity = ChapterMapper.chapterToChapterEntity(chapter);
        ChapterEntity savedEntity = chapterRepository.save(entity);
        return ChapterMapper.chapterEntityToChapter(savedEntity);
    }

    @Transactional
    public void confirmUpload(Long chapterId, List<ChapterPage> chapterPages) {
        ChapterEntity chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ChapterNotFoundException("Chapter not found"));

        List<ChapterPageEntity> entities = chapterPages.stream()
                .map(p -> {
                    ChapterPageEntity entity = PageMapper.pageToPageEntity(p);
                    entity.setChapterId(chapterId);
                    return entity;
                }).collect(Collectors.toList());

        pageRepository.saveAll(entities);

        publisherService.sendChapterPublished(
                chapter.getMangaId(),
                chapter.getId(),
                chapter.getChapterNumber(),
                Instant.now()
        );
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
        return new PagedResponse<>(chapterPages);
    }

    @Transactional
    public void deleteChapter(Long id) {
        ChapterEntity entity = chapterRepository.findById(id)
                .orElseThrow(() -> new ChapterNotFoundException("Chapter not found"));

        String s3FolderPath = String.format("mangas/%d/chapters/%d/", entity.getMangaId(), entity.getId());
        storageService.deleteFolder(s3FolderPath);
        pageRepository.deleteByChapterId(id);
        chapterRepository.delete(entity);
    }
}
