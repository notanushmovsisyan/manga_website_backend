package org.manga_backend.catalogms.domain.service;

import jakarta.transaction.Transactional;
import org.manga_backend.catalogms.api.exception.MangaNotFoundException;
import org.manga_backend.catalogms.api.external.JikanMangaData;
import org.manga_backend.catalogms.api.external.JikanResponse;
import org.manga_backend.catalogms.domain.model.Manga;
import org.manga_backend.catalogms.persistence.Status;
import org.manga_backend.catalogms.persistence.entity.AuthorEntity;
import org.manga_backend.catalogms.persistence.entity.GenreEntity;
import org.manga_backend.catalogms.persistence.entity.MangaEntity;
import org.manga_backend.catalogms.persistence.mapper.MangaMapper;
import org.manga_backend.catalogms.persistence.repository.AuthorRepository;
import org.manga_backend.catalogms.persistence.repository.GenreRepository;
import org.manga_backend.catalogms.persistence.repository.MangaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MangaImportService {
    private final RestClient jikanClient;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final MangaRepository mangaRepository;

    private static final Logger log = LoggerFactory.getLogger(MangaImportService.class);

    public MangaImportService(RestClient jikanClient,
                              AuthorRepository authorRepository,
                              GenreRepository genreRepository,
                              MangaRepository mangaRepository) {

        this.jikanClient = jikanClient;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.mangaRepository = mangaRepository;
    }

    @Transactional
    public Manga importFromMal(Long malId) {
        JikanResponse response = jikanClient.get()
                .uri("/manga/{id}", malId)
                .retrieve()
                .body(JikanResponse.class);

        if (response == null || response.getData() == null) {
            log.warn("MAL import failed: no data returned id={}", malId);
            throw new MangaNotFoundException("Manga with MAL ID " + malId + " not found.");
        }

        JikanMangaData malData = response.getData();
        MangaEntity manga = new MangaEntity();
        manga.setMangaTitle(malData.getTitle());

        if (malData.getImages() != null && malData.getImages().getJpg() != null) {
            manga.setCoverImageUrl(malData.getImages().getJpg().getImage_url());
        }

        manga.setStatus(mapMalStatus(malData.getStatus()));

        if (malData.getAuthors() != null) {
            malData.getAuthors().forEach(malAuthor -> {
                AuthorEntity entity = authorRepository.findByAuthorName(malAuthor.getName())
                        .orElseGet(() -> authorRepository.save(new AuthorEntity(malAuthor.getName())));
                manga.addAuthor(entity);
            });
        }

        if (malData.getGenres() != null) {
            malData.getGenres().forEach(malGenres -> {
                GenreEntity entity = genreRepository.findByGenreName(malGenres.getName())
                        .orElseGet(() -> genreRepository.save(new GenreEntity(malGenres.getName())));
                manga.addGenre(entity);
            });
        }

        MangaEntity mangaEntity = mangaRepository.save(manga);
        log.info("Manga imported successfully id={} title={}", mangaEntity.getId(), mangaEntity.getMangaTitle());
        return MangaMapper.mangaEntityToManga(mangaEntity);
    }

    private Status mapMalStatus(String malStatus) {
        if (malStatus == null) return  Status.PUBLISHING;
        return switch (malStatus) {
            case "Finished" -> Status.FINISHED;
            case "On Hiatus" -> Status.ON_HIATUS;
            default -> Status.PUBLISHING;
        };
    }

}
