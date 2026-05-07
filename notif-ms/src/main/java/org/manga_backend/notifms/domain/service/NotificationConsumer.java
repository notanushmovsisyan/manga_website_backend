package org.manga_backend.notifms.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.manga_backend.notifms.api.dto.BookmarkAddedEvent;
import org.manga_backend.notifms.api.dto.BookmarkRemovedEvent;
import org.manga_backend.notifms.api.dto.ChapterPublishedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final SubscriptionCacheService cacheService;
    private final NotificationInboxService inboxService;
    private final ObjectMapper objectMapper;

    public NotificationConsumer(SubscriptionCacheService cacheService, NotificationInboxService inboxService, ObjectMapper objectMapper) {
        this.cacheService = cacheService;
        this.inboxService = inboxService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "manga-updates",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeChapterPublished(
            @Payload ChapterPublishedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        log.info("Received ChapterPublishedEvent from topic: {}, partition: {}, offset: {}, event: {}", topic, partition, offset, event);

        try {
            Set<String> subscribers = cacheService.getSubscribers(event.mangaId());
            inboxService.createNotificationsForSubscribers(subscribers, event);
            // Acknowledge ONLY after successful processing
            acknowledgment.acknowledge();
            log.info("Successfully processed and acknowledged ChapterPublishedEvent for mangaId: {}", event.mangaId());
        } catch (Exception e) {
            log.error("Failed to process ChapterPublishedEvent for mangaId: {}. Offset will not be committed.", event.mangaId(), e);
            // Without acknowledgment, Kafka will deliver this message again (depending on retry config)
        }
    }

    @KafkaListener(
            topics = "bookmark-updates",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeBookmarkUpdates(
            @Payload String payload,
            @Header(value = "eventType", required = false) String eventType,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        log.info("Received Bookmark update from topic: {}, partition: {}, offset: {}", topic, partition, offset);

        try {
            if (eventType == null) {
                log.warn("Dropped message: Missing 'eventType' header. Payload: {}", payload);
                acknowledgment.acknowledge();
                return;
            }

            switch (eventType) {
                case "BookmarkAdded" -> {
                    BookmarkAddedEvent addedEvent = objectMapper.readValue(payload, BookmarkAddedEvent.class);
                    cacheService.addSubscriber(addedEvent.mangaId(), addedEvent.userId());
                    log.info("Processed BookmarkAdded for user: {}, manga: {}", addedEvent.userId(), addedEvent.mangaId());
                }
                case "BookmarkRemoved" -> {
                    BookmarkRemovedEvent removedEvent = objectMapper.readValue(payload, BookmarkRemovedEvent.class);
                    cacheService.removeSubscriber(removedEvent.mangaId(), removedEvent.userId());
                    log.info("Processed BookmarkRemoved for user: {}, manga: {}", removedEvent.userId(), removedEvent.mangaId());
                }
                default -> log.warn("Dropped message: Unknown eventType '{}'", eventType);
            }

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process bookmark update. Offset will not be committed.", e);
        }
    }
}