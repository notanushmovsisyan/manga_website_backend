package org.manga_backend.notifms.domain.service;

import org.manga_backend.notifms.api.dto.PaginatedNotificationResponse;
import org.manga_backend.notifms.persistence.NotificationEntity;
import org.manga_backend.notifms.persistence.NotificationRepository;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public PaginatedNotificationResponse getUserNotifications(Long userId, int limit, String lastNotificationId) {
        Page<NotificationEntity> page = notificationRepository.findPaginatedByUserId(userId, limit, lastNotificationId);
        String nextNotificationId = null;

        if (page.lastEvaluatedKey() != null && !page.lastEvaluatedKey().isEmpty()) {
            nextNotificationId = page.lastEvaluatedKey().get("notificationId").s();
        }

        return new PaginatedNotificationResponse(page.items(), nextNotificationId);
    }

    public void saveNotification(NotificationEntity notification) {
        notificationRepository.save(notification);
    }
}