package org.manga_backend.contentms.persistence.mapper;

import org.manga_backend.contentms.domain.model.ChapterPage;
import org.manga_backend.contentms.persistence.entity.ChapterPageEntity;

public class PageMapper {

    public static ChapterPageEntity pageToPageEntity(ChapterPage chapterPage) {
        return new ChapterPageEntity(chapterPage.getChapterId(), chapterPage.getPageNumber(), chapterPage.getS3Url());
    }

    public static ChapterPage pageEntityToPage(ChapterPageEntity entity) {
        ChapterPage chapterPage = new ChapterPage(entity.getChapterId(), entity.getPageNumber(), entity.getS3Url());

        if (entity.getId() != null) {
            chapterPage.setId(entity.getId());
        }

        return chapterPage;
    }
}
