package org.manga_backend.catalogms.domain.service;

import jakarta.transaction.Transactional;
import org.manga_backend.catalogms.api.dto.MangaUpdateRequest;
import org.manga_backend.catalogms.api.exception.MangaNotFoundException;
import org.manga_backend.catalogms.domain.model.Manga;
import org.manga_backend.catalogms.persistence.entity.MangaEntity;
import org.manga_backend.catalogms.persistence.mapper.MangaMapper;
import org.manga_backend.catalogms.persistence.repository.MangaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MangaService {
    private final MangaRepository mangaRepository;

    private static final Logger log = LoggerFactory.getLogger(MangaService.class);

    public MangaService(MangaRepository mangaRepository) {
        this.mangaRepository = mangaRepository;
    }

    public List<Manga> getAllMangas() {
        return mangaRepository.findAll()
                .stream()
                .map(MangaMapper::mangaEntityToManga)
                .toList();
    }

    public Manga getMangaById(Long mangaId) {
        MangaEntity mangaEntity =  mangaRepository.findById(mangaId)
                .orElseThrow(() -> {
                    log.warn("Manga not found id={}", mangaId);
                    return new MangaNotFoundException("Manga not found in the catalog.");
                });
        return MangaMapper.mangaEntityToManga(mangaEntity);
    }

    @Transactional
    public Manga updateMangaById(Long mangaId, MangaUpdateRequest request) {
        MangaEntity entity = mangaRepository.findById(mangaId)
                .orElseThrow(() -> {
                    log.warn("Update failed: manga not found id={}", mangaId);
                    return new MangaNotFoundException("Manga with ID " + mangaId + " not found");
                });

        if (request.getMangaTitle() != null && !request.getMangaTitle().isBlank()) {
            entity.setMangaTitle(request.getMangaTitle());
        }

        if (request.getCoverImageUrl() != null && !request.getCoverImageUrl().isBlank()) {
            entity.setCoverImageUrl(request.getCoverImageUrl());
        }

        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }

        MangaEntity updatedEntity = mangaRepository.save(entity);
        return MangaMapper.mangaEntityToManga(updatedEntity);
    }
}
