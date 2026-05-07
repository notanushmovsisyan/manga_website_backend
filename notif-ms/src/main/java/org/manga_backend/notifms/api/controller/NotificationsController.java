package org.manga_backend.notifms.api.controller;

import org.manga_backend.notifms.api.dto.PaginatedNotificationResponse;
import org.manga_backend.notifms.domain.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("#username == authentication.name")
public class NotificationsController {
    private final NotificationService notificationService;

    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<PaginatedNotificationResponse> getNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String lastNotificationId) {

        PaginatedNotificationResponse response = notificationService.getUserNotifications(userId, limit, lastNotificationId);
        return ResponseEntity.ok(response);
    }
}