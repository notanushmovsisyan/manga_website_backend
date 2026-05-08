package org.manga_backend.trackingms.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TrackingRepository {
    private final DynamoDbTable<TrackingEntity> trackingTable;

    private static final Logger log = LoggerFactory.getLogger(TrackingRepository.class);

    public TrackingRepository(DynamoDbTable<TrackingEntity> trackingTable) {
        this.trackingTable = trackingTable;
    }

    public void save(TrackingEntity entity) {
        try {
            trackingTable.putItem(entity);
        } catch (Exception e) {
            log.error("Failed to save tracking entity userId={} mangaId={}",
                    entity.getUserId(),
                    entity.getMangaId(),
                    e);
            throw e;
        }
    }

    public Optional<TrackingEntity> findByUserIdAndMangaId(Long userId, Long mangaId) {
        Key key = Key.builder()
                .partitionValue(userId)
                .sortValue(mangaId)
                .build();
        return Optional.ofNullable(trackingTable.getItem(key));
    }

    public void deleteByUserIdAndMangaId(Long userId, Long mangaId) {
        Key key = Key.builder()
                .partitionValue(userId)
                .sortValue(mangaId)
                .build();
        trackingTable.deleteItem(key);
    }

    public List<TrackingEntity> findBookmarksByUserId(Long userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(userId).
                build());

        return trackingTable.query(queryConditional).items().stream()
                .filter(entity -> Boolean.TRUE.equals(entity.getBookmarked()))
                .collect(Collectors.toList());
    }

    public List<TrackingEntity> findHistoryByUserId(Long userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(userId)
                .build());

        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .scanIndexForward(false)
                .build();

        return trackingTable.index("HistoryIndex")
                .query(request)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }
}
