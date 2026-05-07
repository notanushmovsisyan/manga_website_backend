package org.manga_backend.notifms.persistence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

@Repository
public class NotificationRepository {

    private final DynamoDbTable<NotificationEntity> table;

    public NotificationRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${dynamodb.table.notifications:notifications-table}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(NotificationEntity.class));
    }

    public void save(NotificationEntity notification) {
        table.putItem(notification);
    }

    public Page<NotificationEntity> findPaginatedByUserId(Long userId, int limit, String lastNotificationId) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(userId).build());

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .limit(limit);

        if (lastNotificationId != null && !lastNotificationId.isEmpty()) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("userId", AttributeValue.builder().n(userId.toString()).build());
            startKey.put("notificationId", AttributeValue.builder().s(lastNotificationId).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        return table.query(requestBuilder.build()).iterator().next();
    }
}