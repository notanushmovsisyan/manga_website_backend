package org.manga_backend.notifms.api.dto;

import org.manga_backend.notifms.persistence.NotificationEntity;
import java.util.List;

public record PaginatedNotificationResponse(
        List<NotificationEntity> items,
        String nextNotificationId
) {}