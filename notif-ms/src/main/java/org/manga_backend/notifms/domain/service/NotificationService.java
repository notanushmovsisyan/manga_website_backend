package org.manga_backend.notifms.domain.service;

import org.manga_backend.notifms.api.dto.PaginatedNotificationResponse;
import org.manga_backend.notifms.persistence.NotificationEntity;
import org.manga_backend.notifms.persistence.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public PaginatedNotificationResponse getUserNotifications(Long userId, int limit, String lastNotificationId) {
        Page<NotificationEntity> page = notificationRepository.findPaginatedByUserId(userId, limit, lastNotificationId);
        log.debug("Notifications fetched userId={} count={}", userId, page.items().size());
        String nextNotificationId = null;

        if (page.lastEvaluatedKey() != null && !page.lastEvaluatedKey().isEmpty()) {
            nextNotificationId = page.lastEvaluatedKey().get("notificationId").s();
            log.debug("Pagination nextToken generated userId={} nextId={}", userId, nextNotificationId);
        }
        log.info("Notification request completed userId={} returned={}", userId, page.items().size());
        return new PaginatedNotificationResponse(page.items(), nextNotificationId);
    }

    public void saveNotification(NotificationEntity notification) {
        notificationRepository.save(notification);
    }
}