package org.manga_backend.trackingms.persistence;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

import java.time.LocalDateTime;

@DynamoDbBean
public class TrackingEntity {
    private Long userId;
    private Long mangaId;
    private Long lastReadChapterId;
    private Integer lastReadChapterNumber;
    private LocalDateTime updatedAt;
    private Boolean isBookmarked;

    public TrackingEntity() {}

    @DynamoDbPartitionKey
    @DynamoDbSecondaryPartitionKey(indexNames = "HistoryIndex")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @DynamoDbSortKey
    public Long getMangaId() {
        return mangaId;
    }

    public void setMangaId(Long mangaId) {
        this.mangaId = mangaId;
    }

    @DynamoDbSecondarySortKey(indexNames = "HistoryIndex")
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getLastReadChapterId() {
        return lastReadChapterId;
    }

    public void setLastReadChapterId(Long lastReadChapterId) {
        this.lastReadChapterId = lastReadChapterId;
    }

    public Integer getLastReadChapterNumber() {
        return lastReadChapterNumber;
    }

    public void setLastReadChapterNumber(Integer lastReadChapterNumber) {
        this.lastReadChapterNumber = lastReadChapterNumber;
    }

    public Boolean getBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(Boolean bookmarked) {
        isBookmarked = bookmarked;
    }
}
