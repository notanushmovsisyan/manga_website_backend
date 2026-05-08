package org.manga_backend.notifms.persistence;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;

@DynamoDbBean
public class NotificationEntity {
    private Long userId;
    private String idempotencyKey;
    private String type;
    private Long mangaId;
    private String message;
    private Instant createdAt;

    public NotificationEntity() {}

    @DynamoDbPartitionKey
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    @DynamoDbSortKey
    public String getIdempotencyKey() {return idempotencyKey;}

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getMangaId() { return mangaId; }
    public void setMangaId(Long mangaId) { this.mangaId = mangaId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}