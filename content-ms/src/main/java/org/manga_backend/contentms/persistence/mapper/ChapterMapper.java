package org.manga_backend.contentms.persistence.mapper;

import org.manga_backend.contentms.domain.model.Chapter;
import org.manga_backend.contentms.persistence.entity.ChapterEntity;

public class ChapterMapper {
    public static ChapterEntity chapterToChapterEntity(Chapter chapter) {
        return new ChapterEntity(chapter.getMangaId(),
                chapter.getChapterNumber(),
                chapter.getChapterTitle()
        );
    }

    public static Chapter chapterEntityToChapter(ChapterEntity entity) {
        Chapter chapter = new Chapter(entity.getMangaId(),
                entity.getChapterNumber(),
                entity.getChapterTitle()
        );

        if (entity.getId() != null) {
            chapter.setId(entity.getId());
        }

        if (entity.getCreatedAt() != null) {
            chapter.setCreatedAt(entity.getCreatedAt());
        }

        if (entity.getUpdatedAt() != null) {
            chapter.setUpdatedAt(entity.getUpdatedAt());
        }

        return chapter;
    }
}
