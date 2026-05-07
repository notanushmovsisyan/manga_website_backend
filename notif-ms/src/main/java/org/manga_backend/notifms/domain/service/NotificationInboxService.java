package org.manga_backend.notifms.domain.service;

import org.manga_backend.notifms.api.dto.ChapterPublishedEvent;
import org.manga_backend.notifms.persistence.NotificationEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
public class NotificationInboxService {

    private final NotificationService notificationService;

    public NotificationInboxService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void createNotificationsForSubscribers(Set<String> userIds, ChapterPublishedEvent event) {
        if (userIds == null || userIds.isEmpty()) return;

        String message = String.format("Chapter %d is now available!", event.chapterNumber());

        for (String userIdStr : userIds) {
            NotificationEntity notification = new NotificationEntity();
            notification.setUserId(Long.parseLong(userIdStr));
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setType("CHAPTER_PUBLISHED");
            notification.setMangaId(event.mangaId());
            notification.setMessage(message);
            notification.setCreatedAt(Instant.now());

            notificationService.saveNotification(notification);
        }
    }
}