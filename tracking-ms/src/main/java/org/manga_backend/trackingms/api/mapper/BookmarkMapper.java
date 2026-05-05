package org.manga_backend.trackingms.api.mapper;

import org.manga_backend.trackingms.api.dto.BookmarkRequest;
import org.manga_backend.trackingms.api.dto.BookmarkResponse;
import org.manga_backend.trackingms.domain.model.Bookmark;

public class BookmarkMapper {
    public static Bookmark bookmarkRequestToBookmark(BookmarkRequest request) {
        Bookmark bm = new Bookmark();
        bm.setUserId(request.userId());
        bm.setMangaId(request.mangaId());
        return bm;
    }

    public static BookmarkResponse bookmarkToBookmarkResponse(Bookmark bm) {
        return new BookmarkResponse(
                bm.getUserId(),
                bm.getMangaId(),
                bm.getCreatedAt()
        );
    }
}
